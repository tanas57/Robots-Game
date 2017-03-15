package core;

public class Robot
{
	// robot's inventory informations
	private int weight 		   = 0;
	private int force  		   = 0;
	private int intelligence   = 0;
	private int skill 		   = 0;
	private int speed		   = 0;
	public int torso_force	   = 0;
	public int leg_force	   = 0;
	private double chess_score = 0;
	private double run_score   = 0;
	private double sumo_score  = 0;
	private double pingpong_score = 0;
	public Module [] modules  = new Module[4];
	// Game and Team informations
	public boolean joined_game = false;
	public byte game_queue = 0; // i
	public Team team;
	public byte joined_game_type = 0 ; // chess : 1 , run  : 2 ,  sumo  : 3 , pingpong : 4
	
	// inital
	public Robot(Team tk)
	{
		team = tk;
		calRobotPowers();
	}
	// when team gives command or robots any module is less than 60 durability
	public void divide()
	{
		for (byte i = 0; i < modules.length; i++) {
			team.makeFree(modules[i]); // useable true
			this.modules[i] = null;	   // robot's modules are made null
		}
		team.deactiveRobot(this); // deactive the robot
	}
	// modules add to robot's inventory
	public void addModule(Module md)
	{
		if(md != null){
			for(byte i = 0; i < modules.length; i++){
				if(modules[i] == null){
					modules[i] = md;
					calRobotPowers(); // update robot powers
					break;
				}
			}
		}
	}
	// calculate robots powers include modules
	public void calRobotPowers()
	{
		resetPowers();
		for(byte i = 0; i < modules.length; i++){
			if(modules[i] != null){
				modules[i].getPower(this);
			}
		}
	}
	// reset robots powers 
	private void resetPowers()
	{
		this.force = 0;
		this.intelligence = 0;
		this.weight = 0;
		this.skill = 0;
		this.torso_force =0;
		this.leg_force =0;
		this.speed = 0;
	}
	
	// Robot Class getter and setter functions //
	
	public double getScore(int game)
	{
		// chess : 1 , run  : 2 ,  sumo  : 3 , pingpong : 4
		if(game == 1) return chess_score;
		else if(game == 2) return run_score;
		else if(game == 3) return sumo_score;
		else if(game == 4) return pingpong_score;
		else return 0;
	}
	// print robot's modules and scores
	public String getAllInf()
	{
		String write = " ";
		for(byte i = 0; i < modules.length; i++){
			write += modules[i].getModulType() + "-" + modules[i].getDurability() + "\t";
		}
		write += "( Ch: " + ((int)chess_score > 0 ? (int)chess_score : " - ") + "\tRn: " + ((int)run_score  > 0 ? (int)run_score : " - ") + "\tSm: " + ((int)sumo_score  > 0 ? (int)sumo_score : " - ") + "\tPp: " + ((int)pingpong_score  > 0 ? (int)pingpong_score : " - ") + ")";
		return write;
	}
	
	public void decreaseModulesDurability()
	{
		for(byte i = 0; i < modules.length; i++)
		{
			modules[i].decreaseDurability(2);
		}
		// clear
	}
	
	public void setChessScore(double i) { chess_score = i; }
	
	public void setRunScore(double i) { run_score = i; }
	
	public void setSumoScore(double i) { sumo_score = i; }
	
	public void setPingPongScore(double i) { pingpong_score = i; }
	
	public void setWeight(int w){ weight += w; }
	
	public int getWeight() { return this.weight; }
	
	public void setForce(int f) { force += f; }
	
	public int getForce() { return this.force; }
	
	public void setIntelligence (int i) { intelligence += i; }
	
	public int getIntelligence() { return this.intelligence; }
	
	public void setSkill(int s) { skill += s; }
	
	public int getSkill() { return this.skill; }
	
	public void setSpeed(int s) { speed = s; }
	
	public int getSpeed() { return this.speed; }
}

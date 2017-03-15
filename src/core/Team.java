package core;

public class Team
{
	// intial
	private double credit = 1500.0;
	private final int MAX_MODULES= 20;
	private byte module_num = 0;
	public Robot [] robots = new Robot[9];
	public Module[] modules = new Module[20];
	
	// Teams, Credits getter and setter
	public double getCredit() { return credit; }
	
	public void setCredit(double c, char chose) {
		if( chose == 'i') { credit += c; } // increase credit
		else { credit -= c; } // decrease credit
	}
	// Build a robot if the team not reached maximum robots num 
	public Robot buildRobot(int robotIndex)
	{
		if(robots[robotIndex] == null)
		{
			robots[robotIndex] = new Robot(this); 
			robots[robotIndex].calRobotPowers();
			return robots[robotIndex];
		}
		return null;
	}
	// choosen robot deleted from robots
	public void deactiveRobot(Robot rb)
	{
		for (byte i = 0; i < robots.length; i++) {
			if(robots[i] == rb){
				robots[i] = null;
				break;
			}
		}
	}
	// add to team inventory from robots inventory
	public void makeFree(Module md){
		for (int i = 0; i < modules.length; i++) {
			if(modules[i] == null) { this.addModule(md); break; }
		}
	}
	// print modules
	public void getTeamModules()
	{
		for (byte i = 0; i < this.modules.length; i++) {
			Module m = this.modules[i];
			Game.print("m"+(i < 9 ? "0" + (i+1) : (i+1)) + "");
			if(m != null)
			{
				Game.print("." + m.getModulType() + "-" + m.getDurability() + "		");
			}
			else Game.print("        		");
			if((i+1) % 4 == 0) Game.println("");
		}
		Game.println("\n\n");
		
	}
	
	public boolean addModule(Module md)
	{
		if(module_num < MAX_MODULES){
			for(byte i = 0; i < modules.length; i++)
			{
				if(modules[i] == null){
					modules[i] = md;
					module_num++;
					return true;
				}
			}
		}
		else { Game.println(Error.ERROR_MODULES_NUMS_EXCEEDED.showError()); }
		return false;
	}
	
	public boolean sellModule(Module md)
	{
		int newModuleprice = md.getPrice();
		double priceOfUsedModule = 0.5 * newModuleprice * (double)(md.getDurability() / 100);
		if(removeModule(md)) { this.setCredit(priceOfUsedModule, 'i'); return true; }
		else return false;
	}
	
	public boolean removeModule(Module md)
	{
		for(byte i = 0; i < modules.length; i++){
			if(md == modules[i]){
				modules[i] = null;
				module_num--;
				return true;
			}
		}
		return false;
	}
	
}

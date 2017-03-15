package core;

import java.util.Scanner;
import enigma.core.Enigma;
//import enigma.console.TextAttributes;
//import enigma.core.Enigma;
//import java.awt.Color;
import java.util.Random;

public class Game {
	// initial
	public static enigma.console.Console consol = Enigma.getConsole("Robot Games", 105, 40, false);
	public static Team[] teams;
	public static int week = 1;
	private static byte currentTeam = 0;
	/* GAME DETAILS START */
	private byte game_kind = 0, current_game = 0;
	private String game_name;
	public static Robot[] joined_robots = new Robot[54]; // there can be maximum 54 robots, robots contain team information
	String registering = "--- Game Registering ---\n";
	private byte countJoinedTeam = 0;
	private Robot [][][] team_robots; // it needs to calculate point, robot nums, team nums etc.
	/* GAME DETAILS END */
	
	public Game()
	{
		teams = new Team[6];
		createTeams();
		Start(); // the game is started
	}
	// general metot to use join robots to games
	public void Start()
	{
		Scanner sc = new Scanner(System.in);
		boolean firstScreen = false, wait_command = false, AI_game = true;
		while(true){
			setTeam(Game.currentTeam); // game informations updated

			if(Game.currentTeam == 5) // this is normal player
			{
				AI_game = false; // AI waits "pl" command
				try
				{
					// create basic screen
					if(!firstScreen) { Screen(); firstScreen = true; }
					
					if(registering.length() > 30 ) { System.out.print(registering); registering = "--- Game Registering ---\n"; }
					
					wait_command = true;
					System.out.print("Command => ");
					String cm = sc.nextLine();
							
					Command com = new Command(cm);
					com.Execute();
					
					if(cm.length() == 2 && cm.equals("pl"))
					{	
						// update robots informations
						updateAllRobots();
						playGames(); // game calculations process
						Screen(); // print results
						wait_command = false;
						AI_game = true; // AI can play now
						Game.currentTeam = -1;
					}
					
				}
				catch(Exception ex)
				{
					println(ex.getMessage());
				}
			}
			else{
				// AI process
				if(AI_game) AI();
			}
			
			if(!wait_command) Game.currentTeam++;
				
		}
	}
	public static void joinRobot(Robot rb)
	{
		for(byte i = 0; i < joined_robots.length; i++){
			if(joined_robots[i] == null) { joined_robots[i] = rb; break; }
		}
	}
	// after games played there is a necessary proccess
	private void makeGamed( Robot rb)
	{
		// find robot
		for(byte i = 0; i < teams.length; i++){
			for(byte k = 0; k < teams[i].robots.length; k++){
				if(teams[i].robots[k] == rb){
					// update robots informations to teams

					teams[i].robots[k].joined_game = false;
					teams[i].robots[k].game_queue  = 0;
					teams[i].robots[k].joined_game_type  = 0;
					teams[i].robots[k].decreaseModulesDurability();
				}
			}
		}
	}
	// it calculates the winner of games
	private byte whoWinner(double [] scores)
	{
		double bigger = scores[0]; byte index = 0;
		for(byte i = 0; i < scores.length; i++){
			if(bigger < scores[i]) { bigger = scores[i]; index = i ; }
		}
		return index;
	}
	
	private double getRandomDouble()
	{
		// random 0,950 - 1.050
		Random r = new Random();
		double random_double = 0.0;
		while(true){
					
		int random_int = r.nextInt(1051);
		if(random_int >= 950 && random_int <= 1050) { 
			if(random_int < 1000) random_double = (random_int / 1000.0); 
			else random_double = (random_int / 1000.0);
			break;
			}
						
		}
		return random_double;
	}
	// it is necessary to use robot's scores
	private double calculateRobotScore(Robot rb)
	{
		double result = 0;
		
		//Intelligence *  (HeadDurability/100)
		result = rb.getIntelligence() * ((double)getModuleDurability(rb,1) / 100); // hd: 1, tr: 2, lg: 3, ar: 4
		rb.setChessScore(result);
		
		
		// Speed * (LegDurability/100)
		result = (rb.getSpeed()) * ((double)getModuleDurability(rb,3) / 100 );
		rb.setRunScore(result);
		
		// 100 + TR * 80							100 + LG * 80
		// TorsoForse * (TorsoDurability/100) * 0.7 + LegForce * (LegDurability/100) * 0.3 ) 
		result = ( rb.torso_force * ((double)(getModuleDurability(rb,2) *0.01)) * 0.7 ) + ( rb.leg_force * ((double)(getModuleDurability(rb,3) * 0.01)) * 0.3 ); 
		rb.setSumoScore(result);
		
		// Skill * (ArmDurability/100) * 0.6 
		//+ Intelligence *  (HeadDurability/100) * 0.2 
		//+ Speed * (LegDurability/100) * 0.2 
		result = 
		( rb.getSkill() * ((double)getModuleDurability(rb,4) / 100) * 0.6 ) 
		+ ( rb.getIntelligence() * ((double)getModuleDurability(rb,1) / 100) * 0.2 ) 
		+ ( rb.getSpeed() * ((double)getModuleDurability(rb,3) / 100) * 0.2);
		rb.setPingPongScore(result);
		
		return result;
	}
	// it is different from above method, it is used to AI process
	private double calculateRobotScore(Robot rb, int game)
	{
		double result = 0;
		if(game == 1) // chess
		{
			//Intelligence *  (HeadDurability/100)
			result = rb.getIntelligence() * ((double)getModuleDurability(rb,1) / 100); // hd: 1, tr: 2, lg: 3, ar: 4
		}
		else if(game == 2) // run
		{
			// Speed * (LegDurability/100)
			result = (rb.getSpeed()) * ((double)getModuleDurability(rb,3) / 100 );
		}
		else if(game == 3) // sumo
		{   // 100 + TR * 80							100 + LG * 80
			// TorsoForse * (TorsoDurability/100) * 0.7 + LegForce * (LegDurability/100) * 0.3 ) 
			result = ( rb.torso_force * ((double)(getModuleDurability(rb,2) / 100)) * 0.7 ) + ( rb.leg_force * ((double)(getModuleDurability(rb,3) / 100)) * 0.3 ); 
		}
		else if(game == 4) // pingpong
		{
			// Skill * (ArmDurability/100) * 0.6 
			//+ Intelligence *  (HeadDurability/100) * 0.2 
			//+ Speed * (LegDurability/100) * 0.2 
			result = 
			( rb.getSkill() * ((double)getModuleDurability(rb,4) / 100) * 0.6 ) 
			+ ( rb.getIntelligence() * ((double)getModuleDurability(rb,1) / 100) * 0.2 ) 
			+ ( rb.getSpeed() * ((double)getModuleDurability(rb,3) / 100) * 0.2) - 150;
		}
		return result;
	}
	// game are there, robots and teams scores calculated
	private void playGames()
	{
		byte robo_count = 0; 
		
		// sort according to robots game queue
		team_robots = new Robot[4][6][9]; // Dimension1 : game, Dimension2: team num, Dimension3: robotnum
		
		// joined robots move to new array
		for(byte k = 0; k < joined_robots.length; k++){
			// find robots that are playing robochess
			if(joined_robots[k] != null){
				joined_robots[k].decreaseModulesDurability(); // each module that is using by the robot are decreased 2 point durability
				if(joined_robots[k].joined_game_type == 1){
					team_robots[0][findTeam(joined_robots[k].team)][findRobot(joined_robots[k])] = joined_robots[k];
				}
				if(joined_robots[k].joined_game_type == 2){
					team_robots[1][findTeam(joined_robots[k].team)][findRobot(joined_robots[k])] = joined_robots[k];
				}
				if(joined_robots[k].joined_game_type == 3){
					team_robots[2][findTeam(joined_robots[k].team)][findRobot(joined_robots[k])] = joined_robots[k];
				}
				if(joined_robots[k].joined_game_type == 4){
					team_robots[3][findTeam(joined_robots[k].team)][findRobot(joined_robots[k])] = joined_robots[k];
				}
				joined_robots[k] = null;
			}		
		}
		System.out.println("---- Games ( Results ) ----");
		for(byte i = 0; i < 4; i++)
		{
			
			// game informations updated
			
			this.current_game = i;
			setGameKind();
			
			System.out.print("\n--- " + game_name + " : ");
			
			robo_count = countTeamOrRobot(game_kind - 1 , 2); // count robots;

			countJoinedTeam = countTeamOrRobot(game_kind - 1 , 1); // count teams for calculate prize;
			
			// calculate winner
			if(robo_count == 0) { 
				System.out.println( "(0 team)" ); 
				System.out.println(Error.NO_WINNER.showError()); 
			} // no winner
			else if(robo_count == 1) 
			{ // directly a team winner 
				
			System.out.println( "(1 team)" );
			
			for(byte g = 0; g < teams.length; g++){
				for(byte h = 0; h < teams[g].robots.length; h++){
					if(team_robots[game_kind-1][g][h] != null){
						
						// out the game and decrease durability
						makeGamed(team_robots[game_kind-1][g][h]);
						
						team_robots[game_kind-1][g][h].team.setCredit(getPrize(),'i'); // prize added current team

						//calculateRobotScore(team_robots[game_kind-1][g][h]); // score calculated and changed
						
						System.out.println("t" + (g+1) + "-r" + (h+1) + " : " + team_robots[game_kind-1][g][h].getAllInf());
						
						double random_double = getRandomDouble();
						
						System.out.println( "t" + (findTeam(team_robots[game_kind-1][g][h].team) + 1) + " score =  (" + (int)team_robots[game_kind-1][g][h].getScore(game_kind) + " * " + random_double + ") = " + ((int)team_robots[game_kind-1][g][h].getScore(game_kind) * random_double));
						System.out.println("Team " + (findTeam(team_robots[game_kind-1][g][h].team) + 1) + " is winner");
						
						
						break;
					}
				}
			}
			// hd: 1, tr: 2, lg: 3, ar: 4
			}
			else{ 
				// more than one robot
				System.out.println("(" + countJoinedTeam + " teams)");
				
				double [] scores = null;
				double [] robot_queue_score = null;
				String write = "\n";
				scores = new double[6]; // team scores
				for(byte g = 0; g < teams.length; g++){
					
					// in a game, there can be one more than robots
					robot_queue_score = new double[9]; // max 9 robots
					
					byte upper = 0, counter = 0;
					// reset parameters
					double score_temp = 0;
					
					for(byte h = 0; h < teams[g].robots.length; h++){
						if(team_robots[game_kind-1][g][h] != null){
							
							// First show robots informations, and then calculate team score
							
							double robot_score = calculateRobotScore(team_robots[game_kind-1][g][h]);
							
							robot_queue_score[team_robots[game_kind-1][g][h].game_queue] = robot_score; 
							
							// score calculated and changed;

							score_temp = robot_score;
							// out the game and decrease durability
							makeGamed(team_robots[game_kind-1][g][h]);
							
							System.out.println("t" + (g+1) + "-r" + (h+1) + " : " + team_robots[game_kind-1][g][h].getAllInf());
							
							counter++;
						}
					}

					boolean first = false;
					double random_double = getRandomDouble();
					if(counter > 1){
						
						write += "t" + ( g + 1) + " score = ( ";
						for(byte q = 0; q < robot_queue_score.length; q++){
							if(robot_queue_score[q] > 0){
								//scores[g] += 
								if(!first){
									write += (int)robot_queue_score[q] + " + ";
									scores[g] += robot_queue_score[q];
									first = true;
								}
								else {
									upper += 4;
									write += " + " + ((int)robot_queue_score[q] + " / " + upper);
									scores[g] += robot_queue_score[q] / upper;
								}
							}
						}
						write += " ) * " + random_double + " = " + ((int)scores[g] * random_double) + "\n";
					}
					else { 
						if(score_temp > 0){
						scores[g] = score_temp * random_double;
						write += "t" + (g+1) + " score = ( " + (int)score_temp + " ) * " + random_double + " = " + scores[g] + "\n";
						}
					}
					counter = 0;
				}
				System.out.println(write);
				System.out.println("Team " + (whoWinner(scores) + 1) + " is winner");
				teams[whoWinner(scores)].setCredit(getPrize(),'i'); // prize added current team
			}
			robo_count = 0; // reset
			
		}
		increaseWeek(); 
	}
	
	private byte countTeamOrRobot(int game, int kind)
	{
		// kind 1 : count team
		byte count_team = 0, count_robot = 0, temp_robot_count = 0;
		for(byte i = 0; i < teams.length; i++){
			if(count_robot != temp_robot_count) { count_team++; temp_robot_count = count_robot; }
			for(byte k = 0; k < teams[i].robots.length; k++){
				if(team_robots[game][i][k] != null){
					count_robot++;
				}
			}
		}
		if(kind == 1) return count_team++;
		else if(kind == 2) return count_robot++;
		
		return 0;
	}
	// according to choosen module type get this durability
	private int getModuleDurability(Robot rb, int type)
	{
		// hd: 1, tr: 2, lg: 3, ar: 4
		for(byte i = 0; i < rb.modules.length; i++)
		{
			if(rb.modules[i].Modul_Type() == type) return rb.modules[i].getDurability();
		}
		return 0;
	}
	// find team index from general team index
	private byte findTeam(Team tm)
	{
		for(byte i = 0; i < teams.length; i++){
			if(teams[i] == tm) return i;
		}
		return 0;
	}
	// find robot index from related team's robots
	private byte findRobot(Robot rb){
		for(byte i = 0; i < rb.team.robots.length; i++){
			if(rb.team.robots[i] == rb) return i;
		}
		return 0;
	}
	// game names and game kind determined there
	private void setGameKind()
	{
		if(this.current_game % 4 == 0) { game_kind = 1; game_name = "RoboChess"; }
		else if(this.current_game % 4 == 1) { game_kind = 2; game_name = "RoboRun"; }
		else if(this.current_game % 4 == 2) { game_kind = 3; game_name = "RoboSumo"; }
		else if(this.current_game % 4 == 3) { game_kind = 4; game_name = "RoboPingPong"; }
		else game_kind=-1;
	}
	// print basic screen
	private void Screen()
	{
		System.out.print("		Week:"+ Game.week + " Robot/Credit: "); 
		TeamRobotsAndCredit(); // get teams robot nums and credit informations
	}
	// each team defined a new team
	private void createTeams()
	{ 
		for(byte i = 0 ; i < 6; i++) teams[i] = new Team(); 
	} 
	// set current team
	public void setTeam(byte num) { if(num >= 0 && num <=5) currentTeam = num; }
	// get current team	
	public static Team getTeam()
	{
		if(currentTeam >= 0 && currentTeam <= 5) { if(teams[currentTeam] != null) return teams[currentTeam]; else return null;}
		else return null;
	}
	// get current team num	
	public static byte getCurrentTeam()
	{
		return (byte) (currentTeam + 1);
	}
	// print information to screen
	private void TeamRobotsAndCredit()
	{
		// count each team's active robot
		byte counter = 0;
		for(byte i = 0; i < teams.length; i++)
		{
			for(byte j = 0; j < teams[i].robots.length; j++)
			{
				if(teams[i].robots[j] != null) counter++;
			}
			System.out.print("T" + (i+1) + ":" + counter + "/" + ((int)teams[i].getCredit()) + " ");
			counter = 0;
		}
		System.out.println("\n\n");
	}
	// Artificial Intelligence is there
	private void AI()
	{
		/* COUNT ACTIVE ROBOTS NUM */
		Team team = teams[getCurrentTeam() - 1];
		int counter = 0;
		for (int i = 0; i < team.robots.length; i++) {
			if(team.robots[i] != null) counter++;
			
		}
		/* COUNT ACTIVE ROBOTS NUM */
		
		/* BUY ROBOTS OR MODULES */
		
		if(counter == 0){
			
			Command buy_module;
			// firstly, team has not any robot, team must buy some modules
			int end = getRandNum(5) + 5;
			while(end > 1){
				buy_module = new Command("by tr" + getRandNum(3)); buy_module.Execute();
				buy_module = new Command("by hd" + getRandNum(4)); buy_module.Execute();
				buy_module = new Command("by lg" + getRandNum(3)); buy_module.Execute();
				buy_module = new Command("by ar" + getRandNum(5)); buy_module.Execute();
					
				end--;
			}
			
		}
		else
		{
			// control robots modules durability, if there is a low durability, related module sell, and changed same module type
			for (int i = 0; i < team.robots.length; i++) {
				if(team.robots[i] != null){
					for (int j = 0; j < team.robots[i].modules.length; j++) {
						if(team.robots[i].modules[j] != null)
						{
						Module cur = team.robots[i].modules[j];
						
							if(cur.getDurability() < 76){

								boolean isExitst = false;
								byte change_mdl = 0;
								for (int k = 0; k < team.modules.length; k++) {
									if(team.modules[k] != null){
										
										if(team.modules[k].getModulType().substring(0,2).equals(cur.getModulType().substring(0,2)))
										{
											isExitst = true; change_mdl = (byte) k;
										}
									}
								}
								if(isExitst)
								{
									// team has same module type, change each other
									change_mdl++;
									Command change = new Command("ch r" + ( i + 1 ) + " m" + ((change_mdl >9) ? change_mdl : "0" + change_mdl));
									change.Execute();
								}
								else{
									// team has not related module, buy new one
									Command buy = new Command("by " + cur.getModulType().substring(0,2) + getRandNum(3));
									buy.Execute();
								}
							}
						}
					}
				}
			}
			
			int end = getRandNum(10);
			// inventory check
			// missing modules are completed and a robot can be created they are using 
			while(end > 1)
			{
				byte[] own_module_counter = new byte[4];
				
				for (byte i = 0; i < team.modules.length; i++) {
					if(team.modules[i] != null){
						own_module_counter [(team.modules[i].Modul_Type() - 1)]++;
					}
				}
				byte max = own_module_counter[0];
				for (byte i = 0; i < own_module_counter.length; i++) {
	
					if(own_module_counter[i] > max) max = own_module_counter[i];
						
				}
					
				for (byte i = 0; i < own_module_counter.length; i++) {
					if(own_module_counter[i] < max){
						// bu modül eksik robot yapamıyor satın al
						Command buy = new Command("by " + AI_MODULE_TYPE((byte)(i+1)) + getRandNum(3));
						buy.Execute();
					}
				}
				
				// reset
				for (int i = 0; i < own_module_counter.length; i++) {
					own_module_counter[i] = 0;
				}
				end--;
			}
		}
		
		/* USE MODULES TO ROBOTS */
		while(true)
		{
			// available modules added to robot and created a new robot
			String add_modules = "";
			int module_control_counter = 0;
			
			for(byte b = 0; b < team.modules.length; b++){
				if(team.modules[b] != null)
				{
					Module current = team.modules[b];
					if(current.getDurability() > 60){
						// related module durability must greater than 60
						if(!add_modules.contains("tr")){
							module_control_counter += current.Modul_point();
							add_modules += current.getModulType() + " ";
						}
						else if(!add_modules.contains("ar"))
						{
							module_control_counter += current.Modul_point();
							add_modules += current.getModulType() + " ";
						}
						else if(!add_modules.contains("hd"))
						{
							module_control_counter += current.Modul_point();
							add_modules += current.getModulType() + " ";
						}
						else if(!add_modules.contains("lg"))
						{
							module_control_counter += current.Modul_point();
							add_modules += current.getModulType() + " ";
						}

					}
					else {
						// sell this module
						team.sellModule(current);
					}
					
				}
				if(module_control_counter == 1087) break;
			}
			if(module_control_counter == 1087){
				// add_modules has 4 modules that are different each other
				AI_ADD_ROBOT(add_modules.substring(0,15));
				module_control_counter = 0;
				continue;
			}
			else break;
			
		}
		/* USE MODULES TO ROBOTS */
		
		/*
		 * intelligence => chess
		 * speed 		=> run
		 * force 		=> sumo
		 * skill 		=> pingpong 
		 */
		
		/* JOIN GAMES */
		double [][] reg_game = new double[6][9];
		//calculateRobotScore
		for(byte a = 0; a < team.robots.length; a++)
		{
			Robot current = team.robots[a];
			if(current != null){
				double max_score = calculateRobotScore(current, 1);
				int game_type = 1;
				// chess : 1 , run  : 2 ,  sumo  : 3 , pingpong : 4
				for(byte b = 0; b < 4; b++){ // the robot must join a game which is the most successfull from the robot
					// 1 : chess, 2 : run, 3 : sumo, 4 : pingpong
					if( calculateRobotScore(current, 1 + b) > max_score )
					{
						max_score = calculateRobotScore(current, 1 + b);
						game_type = b + 1;
					}
				}
				reg_game[game_type][a] = max_score;
			}
		}
		
		char[] game_types = new char [] { ' ', 'c', 's', 'r', 'p' };
		registering += ("Team : " + getCurrentTeam() + " ");
		for (int i = 1; i <=5; i++) {
			for (int j = 0; j < 9; j++) {
				if(reg_game[i][j] > 0 && team.robots[j] != null && !team.robots[j].joined_game){
					
					Command reg = new Command("rg r" + (j + 1) +  " > " + (game_types[i]) + (j+1));
					registering += ("r" + (j + 1) +  " > " + (game_types[i]) + (j+1) + "\t");
					reg.Execute();
				}
			}
		}
		registering += "\n";
		/* JOIN GAMES */
	}
	
	// JUST NEEDED AI FUNCTIONS //
	private void AI_ADD_ROBOT(String cmd)
	{
		for(byte d = 0; d < teams[getCurrentTeam() - 1].robots.length; d++){
			if(teams[getCurrentTeam() - 1].robots[d] == null){
				
				Command ai = new Command("++ r" + (d + 1) + " = " + cmd);
				ai.Execute();
				break;
			}
		}
	}
	
	private String AI_MODULE_TYPE(byte type)
	{
		if(type == 1) return "hd"; 
		else if(type == 2) return "tr";
		else if(type == 3) return "lg";
		else if(type == 4) return "ar";
		else return ""; 
	}
	
	private int getRandNum(int i)
	{
		Random rd = new Random();
		while(true){
			int newnum = rd.nextInt(i);
			if(newnum > 0) { return newnum; }
		}
	}
	
	private void increaseWeek()
	{
		// each week decrease modules durability from each modules
		for(byte i = 0; i < teams.length; i++)
		{
			for(byte j= 0; j < teams[i].modules.length; j++)
			{
				if(teams[i].modules[j] != null)
				teams[i].modules[j].decreaseDurability(2);
			}
		}

		// reset gamer robots
		for(byte i = 0; i < joined_robots.length; i++){
			joined_robots[i] = null;
		}
		// durability control
		for(byte i = 0; i < teams.length; i++){
			for (byte j = 0; j < teams[i].robots.length; j++) {
				if(teams[i].robots[j] != null){
					// control robots modules durability
					for(byte k = 0; k < teams[i].robots[j].modules.length; k++){
						if(teams[i].robots[j].modules[k] != null){
							if(teams[i].robots[j].modules[k].getDurability() < 60){
								// divide the robot
								teams[i].robots[j].divide();
								teams[i].robots[j] = null;
								break;
							}
						}
					}
				}
			}
		}
		// increase week
		Game.week++;
	}
	// robots scores informations updated
	private void updateAllRobots()
	{
		for (int i = 0; i < teams.length; i++) {
			for (int j = 0; j < teams[i].robots.length; j++) {
				if(teams[i].robots[j] != null) calculateRobotScore(teams[i].robots[j]);
			}
		}
	}
		
	private double getPrize()
	{
		int team_num = countJoinedTeam; // oyuna katılan takım sayısı olacak
		double prize = 0;
		switch(game_kind){
		case 1: prize = (200 + (team_num * 25)); break;
		case 2: prize = (200 + (team_num * 30)); break;
		case 3: prize = (200 + (team_num * 35)); break;
		case 4: prize = (200 + (team_num * 40)); break;
		}
		return prize;
	}
	
	/* just show error messages to user not computer */
																							
	public static void println(Object data) { if(Game.getCurrentTeam() == 6) System.out.println(data); } 
	
	public static void print(Object data) { if(Game.getCurrentTeam() == 6) System.out.print(data); } 
	
}

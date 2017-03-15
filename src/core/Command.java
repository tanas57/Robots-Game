package core;

public class Command {
	private final String MODULE_HEAD  = "hd";
	private final String MODULE_TORSO = "tr";
	private final String MODULE_LEG	  = "lg";
	private final String MODULE_ARM	  = "ar";
	private Team team;
	private String command;

	public Command(String cm)
	{
		team = Game.getTeam();
		command = cm;
	}
	
	public void Execute()
	{
		int lenght = command.length(); // command lenght
		if( lenght < 1 )
		{
			Game.println(Error.ERROR_EMPY_COMMAND.showError());
		}
		else if(lenght > 2 ) 
		{
			String firstCMD = getCmd(command);
			if(!firstCMD.equals("invalid")) splitCommand( firstCMD, command);
		}
		else{ Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
	}
	// command controled there
	private String getCmd(String cm)
	{
		String [] cmd = new String[] { "by", "sl", "++", "--", "ch", "ls", "rg" };
		
		String MdlType = cm.substring(0, 2);
		//Game.println(MdlType);
		for(byte i = 0; i < cmd.length; i++)
		{
			if(MdlType.equals(cmd[i])) return MdlType;
		}
		Game.println(Error.ERROR_INVALID_COMMAND.showError());
		return "invalid";
	}
	// command makes usable there 
	private void splitCommand(String com, String detail)
	{
		if(com.equals("by"))
		{
			// user can buy a new module command lenght must be 6 words
			if( detail.length() == 6){
				// command is true
				String module  = detail.substring(3, 5); // get module type
				int level 	   = Character.getNumericValue(detail.charAt(5)); // get module level
				
				boolean control = moduleNameControl(module);
				
				if(control){
					
					if( level >= 1 && level <= 6 && (detail.charAt(2) == ' '))
					{
						Module md = new Module(module, level);
						// modül ekliyecez sadece takıma robota değil.
						buyModule(md);
						Game.print(" => Respond : module added to inventory. Current credit : " + team.getCredit() + " \n");
					}
					else { Game.println(Error.ERROR_OUT_OF_MODULE_LEVEL.showError()); }
				}
				else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
				
			}
			else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
		}
		else if(com.equals("sl"))
		{
			// sell a module
			try
			{
				if( detail.length() == 6)
				{
					String lv = detail.substring(4,6); // get module level
					int level  = Integer.parseInt(lv);
					
					if(detail.charAt(3) == 'm'){
						
						if( level >= 1 && level <= 20 && (detail.charAt(2) == ' '))
						{
							// search module
							Module md = team.modules[level-1];
							if( md != null){
								int newModuleprice = md.getPrice();
								double priceOfUsedModule = 0.5 * newModuleprice * (double)(md.getDurability() / 100);
								if(sellModule(md)) { team.setCredit(priceOfUsedModule,'i'); Game.print(" => Respond : deleted and " + priceOfUsedModule + " credits added. Current Credit : " + team.getCredit() + " \n"); }
							}
							else { /* not found */ }
							
						}
						else { Game.println(Error.ERROR_OUT_OF_MODULE_LEVEL.showError()); }
					}
					else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
				}
				else if( detail.length() == 5)
				{
					// sell a robot => divide to modules
					int robot_num = Character.getNumericValue(detail.charAt(4));
					if(detail.charAt(2) == ' ' && detail.charAt(3) == 'r' && robot_num >= 1 && robot_num <= 6)
					{
						if(team.robots[robot_num - 1] != null){
							double counter = 0;
							for(byte a = 0; a < team.robots[robot_num - 1].modules.length; a++){
								Module md = team.robots[robot_num - 1].modules[a];
								counter += 0.5 * md.getPrice() * ((double)md.getDurability() / 100);
								team.robots[robot_num - 1].modules[a] = null;
							}
							team.robots[robot_num - 1] = null;
							team.setCredit(counter,'i');
							Game.print(" => Respond : deleted and " + counter + " credits added. Current Credit : " + team.getCredit() + " \n");
						}
					}
					else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
				}
				else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
			}
			catch(Exception ex) { Game.println(ex.getMessage()); }
		}
		else if(com.equals("++"))
		{
			try
			{
				
				if(detail.length() == 23){ // a robot must contain 4 modules
					int rb_num = Character.getNumericValue(detail.charAt(4));
					if( (detail.charAt(2) == ' ') && (detail.charAt(5) == ' ') && (detail.charAt(3) == 'r') && (detail.charAt(6) == '=') && (detail.charAt(7) == ' ') && (rb_num >=1 && rb_num <= 9) )
					{
							int module_counter = 0; // for each module control, if sum is 10 all module there is
							
							String mdls = detail.substring(8);
							String [] added_modules = mdls.split(" ");
							
							Module [] new_modules = new Module[4]; // max 4 modules
							Module [] own_modules = new Module[4]; // 
							boolean robot_create_control = true, empty_module = false;
							
							/* modules control */
							if(added_modules != null){
								for(byte i = 0 ; i < added_modules.length; i++){
									if(added_modules[i].charAt(0) == 'm')
									{
										// this module is in related team's inventory
										String lv = added_modules[i].substring(1,3); // get module level
										int level  = Integer.parseInt(lv);
										
										if(level >=1 && level <= 20){
											if(team.modules[level-1] != null)
											{
												if(team.modules[level-1].getDurability() >= 60){
												module_counter += team.modules[level-1].Modul_point();
												own_modules[i]  = team.modules[level-1];
												}
												else Game.println(Error.ERROR_MODULE_DURABILITY.showError());												
											}
										}
										else{ Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
									}
									else{
										// new module
										Module[] durability_control = new Module[20]; // maximum module nums, it is needed sort modules according to durability
										byte counter = 0;
										
										String current_module_type = added_modules[i].substring(0, 2);
										
										int current_module_level   = Character.getNumericValue(added_modules[i].charAt(2));
										
										if(moduleNameControl(current_module_type) && (current_module_level >= 1 && current_module_level <= 6))
										{
											// valid module search inventory
											for(byte a = 0; a < team.modules.length; a++){
												if(team.modules[a] != null && team.modules[a].getModulType().equals((current_module_type+current_module_level))){
													durability_control[counter] = team.modules[a]; // will be controled
													counter++;
												}
											}
											if(counter > 0){
											Module biggestDurability = durability_control[0];
											for(byte r = 0; r < durability_control.length; r++){
												if(durability_control[r] != null){
													
													if(durability_control[r].getDurability() > biggestDurability.getDurability()) biggestDurability = durability_control[r];
												}
											}
											new_modules[i] = biggestDurability;
											
											module_counter += new_modules[i].Modul_point();
											}
											else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); empty_module = true; break; }
										}
										else { robot_create_control = false; Game.println(Error.ERROR_INVALID_COMMAND.showError());  break; /* module is invalid */ }
									
									}
								}
							}
							/* modules control */
							
							/* add to robot */
							if(robot_create_control && (module_counter == 1087) && !empty_module){ 
								
								// why 1087 ? each different module type values has determined random numbers, that are count is 1087
								
								// each different module there is, we can create a robot
								
								Robot rb = team.buildRobot(rb_num - 1); // robot added related team
								
								if(rb != null){ // perhaps, there may be maximum robots num error
									// control modules which will buy | is team credit enough?
									
									Module md = null;
									for(byte a = 0; a < 4; a++) // a robot has 4 modules
									{
										if(new_modules[a] != null )
										{
											md = new_modules[a];
											rb.addModule(md);
											makeUsedModule(md); 
										}
										if(own_modules[a] != null )
										{
											md = own_modules[a];
											rb.addModule(md); 
											makeUsedModule(md); 
										}
									}
									rb.calRobotPowers(); // get powers
									Game.print(" => Respond : Robot " + rb_num + " has been added" + " \n");
								}
								else { Game.println(Error.ERROR_ROBOT_ALREADY_BUILDED.showError());}
							}
							else { Game.println(Error.ERROR_ROBOT_NOT_AVAILABLE.showError()); /* each module must be different each other */ }
						}
						else{ Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
					}
					else { Game.println(Error.ERROR_INVALID_COMMAND.showError());  }
				
			}
			catch(Exception ee) { Game.println(ee.getMessage()); }
			
		}
		else if(com.equals("--"))
		{
			//divide a robot         
			if(detail.length() == 5)
			{
				int robot_num = Character.getNumericValue(detail.charAt(4));
				if(detail.charAt(2) == ' ' && detail.charAt(3) == 'r' && (robot_num >= 1 && robot_num <=9))
				{
					if(team.robots[robot_num - 1] != null)
					{
						
						team.robots[robot_num - 1].divide(); // divide the robot
						Game.print(" => Respond : Robot " + robot_num + " has been divided" + " \n");
					}
					else{ Game.println(Error.ERROR_ROBOT_NOT_AVAILABLE.showError()); }
				}
				else{ Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
			}
			else{ Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
		}
		else if(com.equals("ch"))
		{
			if(detail.length() == 9){
				
            String lv = detail.substring(7,9); // get module level
            
            int level = 0;
            try
            {
            level = Integer.parseInt(lv);
            }
            catch(Exception ee) { Game.println(ee.getMessage()); }
			int robotlevel=Character.getNumericValue(detail.charAt(4));
				if( (detail.charAt(2) == ' ') && (detail.charAt(5) == ' ') && (detail.charAt(3) == 'r') && (detail.charAt(6) == 'm')  && (level >=1 && level <= 20) && (robotlevel >=1 && robotlevel <=9) )
				{
					// look entered module compare with robot has
					if(team.modules[level-1] != null) // modul must be available
					{

						Module module_entered = team.modules[level-1];
						byte module_type = module_entered.Modul_Type();
						
						// find related module of robot
						if(team.robots[robotlevel-1] != null){
							for(byte a = 0; a < team.robots[robotlevel-1].modules.length; a++){
								if(team.robots[robotlevel-1].modules[a].Modul_Type() == module_type){
									// entered module and related module found change them
									
									Module temp_change = team.robots[robotlevel-1].modules[a]; // this module goes to team inventory
									
									team.addModule(temp_change); // module that is in team's inventory goes to robot's inventory
									
									team.robots[robotlevel-1].modules[a] = module_entered; // robot's related module changed
									
									team.removeModule(module_entered); // in inventory, related module has deleted
									
									Game.print(" => Respond : Robot " + robotlevel + " module has been changed");
								}
							}
						}
						else Game.println(Error.ERROR_ROBOT_NOT_AVAILABLE.showError());	
					}
					else Game.println(Error.ERROR_MODULE_NULL.showError());	
				}
				else{ Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
			}
			else{ Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
		}
		else if(com.equals("ls"))
		{
			// command lenght must be 4 words
			if( detail.length() == 4)
			{
					// command is true
					int teamNum = Character.getNumericValue(detail.charAt(3)); // get module level
					if(teamNum >= 1 && teamNum <= 6 && detail.charAt(2) == ' ')
					{
						// list team modules
						
						Robot[] rb = Game.teams[teamNum-1].robots;
						if(rb != null){
							Game.println("--- Team " + teamNum + ": Robots ---");
							for(byte i = 0; i < rb.length; i++){
								if(rb[i] != null){
									if(rb[i].modules != null){
										Game.print("Rb" + (i+1) + " Modules : ");
										Game.println(rb[i].getAllInf());
										Game.println("");
									}
								}
							}
						}
						else {Game.println(Error.ERROR_ROBOT_NULL.showError()); }
						System.out.println("\n");
						// modules
						System.out.println("Team " + teamNum + " Modules \n -------------------------------");
						Team tm = Game.teams[teamNum - 1];
						tm.getTeamModules();
						// modules
					}
					else { Game.println(Error.ERROR_TEAM_NUM.showError());}
			}
			else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
		}
		else if(com.equals("rg"))
		{
			// register robot to game   
			if(detail.length() == 10)
			{
				if(detail.charAt(2) == ' ' && detail.charAt(3) == 'r' && detail.charAt(5) == ' ' && detail.charAt(6) == '>' && detail.charAt(7) == ' ')
				{
					int robot_num = Character.getNumericValue(detail.charAt(4));
					if(robot_num >= 1 && robot_num <=9)
					{
						char game = detail.charAt(8);
						if(game == 'c' ||  game == 's' || game == 'r' || game == 'p')
						{
							int game_num = Character.getNumericValue(detail.charAt(9));
							if(game_num >= 1 && game_num <=9)
							{
								// get robot inf.
								if(team.robots[robot_num - 1] != null && team.robots[robot_num - 1].joined_game == false)
								{
									boolean control = true;
									for(byte a = 0; a < Game.joined_robots.length; a++){
										// control robot, is it in game ?	
										if(Game.joined_robots[a] != null ){
													
												if(Game.joined_robots[a].team == team && Game.joined_robots[a].joined_game_type == getGameType(game) && Game.joined_robots[a].game_queue == game_num - 1){
													// do not add due to the robot has already added
													control = false;
												}
												else control = true; // add to game
											}
										}
									
									if(control){
										// check game queue
										// the robot has joined chosen game
										team.robots[robot_num - 1].joined_game = true;
										team.robots[robot_num - 1].game_queue = (byte)(game_num - 1);
										team.robots[robot_num - 1].joined_game_type = getGameType(game);
										
										Game.joinRobot(team.robots[robot_num - 1]);
										Game.print(" => Respond : Robot has been added to related game");
									}
									else { Game.println(Error.ERROR_GAME_NUM_ENTERED.showError()); }
								}
								else Game.println(Error.ERROR_ROBOT_NOT_AVAILABLE.showError());
							}
							else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
						}
						else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
					}
					else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
				}
				else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
			}
			else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }
		}
		else { Game.println(Error.ERROR_INVALID_COMMAND.showError()); }

	}
	// get game type to use some proccess
	private byte getGameType(char game)
	{
		switch(game){
		// chess : 1 , run  : 2 ,  sumo  : 3 , pingpong : 4
		case 'c' : return 1;
		case 'r' : return 2; 
		case 's' : return 3;
		case 'p' : return 4;
		default  : return 0;
		}
	}
	// control entered module names
	private boolean moduleNameControl(String mdl_name){
		String[] modules = new String[] { MODULE_HEAD, MODULE_TORSO, MODULE_LEG, MODULE_ARM };
		boolean control = false;
		// girilen modül doğru mu diziden karşılaştır
		for(byte i = 0; i < modules.length; i++)
		{
			if(modules[i].equals(mdl_name)) { control = true; break;  }
		}
		
		return control;
	}
	
	private boolean buyModule(Module md)
	{
		switch(md.Modul_Type())
		{
			case 1: /* buy head  */ return md.buyHead(team);
			case 2: /* buy torso */ return md.buyTorso(team); 
			case 3: /* buy legs  */ return md.buyLeg(team); 
			case 4: /* buy arms  */ return md.buyArm(team); 
			case 0: Game.println(Error.ERROR_INVALID_COMMAND.showError()); return false;
		}
		return false;
	}
	
	private boolean sellModule(Module m)
	{
		for(byte i = 0; i < team.modules.length; i++){
			if(team.modules[i] == m){
					// delete directly
					team.removeModule(m);
					return true;
			}
			else { /* module not found */ }
		}
		return false;
	}
	
	private boolean makeUsedModule(Module md)
	{
		for(byte i = 0; i < team.modules.length; i++){
			if(team.modules[i] == md){ team.removeModule(md); return true; }
		}
		return false;
	}

}

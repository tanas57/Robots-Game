package core;

public enum Error {
	ERROR_EMPY_COMMAND("Error : Command is empty, enter a command"), 
	ERROR_INVALID_COMMAND("Error: Command is invalid"),
	ERROR_OUT_OF_MODULE_LEVEL("Error : Module level is invalid"),
	ERROR_MODULES_NUMS_EXCEEDED("Error : A team can not buy more than 20 modules"),
	ERROR_ROBOTS_NUMS_EXCEEDED("Error : A team can not buy more than 9 robots"),
	ERROR_MODULE_NULL("Error : Entered module is not found"),
	ERROR_MODULE_DURABILITY("Error : Chosen a module's durability is not enough, please change it"),
	ERROR_ROBOT_NULL("Error : Team has not any robot"),
	ERROR_ROBOT_ALREADY_BUILDED("Error : The robot has already built"),
	ERROR_ROBOT_NOT_AVAILABLE("Error : The robot is not available"),
	ERROR_TEAM_NUM("Error: Team id must be numerical between 1 and 6"),
	ERROR_TEAM_MAX_MODULES("Error : Team has reached maximum modules"),
	ERROR_USED_MODULE("Error : Module which is in inventory has already used"),
	ERROR_INSUFFICIENT_CREDIT("Error : Team credit not enough"),
	ERROR_GAME_NUM_ENTERED("Error : A robot has already added to choosen game queue, can try next num"),
	NO_WINNER("Winner: No winner. Prize transferred to the next week.");
	//"Error : Command is not accepted, try again."
	private String errExplain;
	
	private Error(String ex)
	{
		this.errExplain = ex;
	}
	public String showError()
	{
		return errExplain;
	}
}

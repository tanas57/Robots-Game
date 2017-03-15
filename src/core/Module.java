package core;

public class Module {
	
	private final int TORSO_PRICE = 150;
	private final int HEAD_PRICE  = 100;
	private final int LEG_PRICE   = 50;
	private final int ARM_PRICE   = 40; // her levelde iki katı fiyat.
	private String module_type = "";
	private int module_level = 1;
	private byte durability = 100;
	
	public Module() { /* */ }
	
	public Module(String type, int level)
	{
		// module type is defined there
		module_type  = type;
		module_level = level;
	}
	
	public byte Modul_Type()
	{
		if(module_type.equals("hd")) return 1; 
		else if(module_type.equals("tr")) return 2;
		else if(module_type.equals("lg")) return 3;
		else if(module_type.equals("ar")) return 4;
		else return 0; 
	}
	
	public String getModulType() { return this.module_type+module_level; }
	
	public int getDurability() { return this.durability; }
	
	public int getModuleLevel() { return this.module_level; }
	
	public void decreaseDurability(int i) { this.durability -= i; }
	
	public boolean buyTorso(Team tk)
	{
		// decrease costs 
		double price = getPrice();
		if(tk.getCredit() >= price){
			if(tk.addModule(this)){
				tk.setCredit(price,'d');
			return true;
			}
			else { Game.println(Error.ERROR_TEAM_MAX_MODULES.showError()); }
		}
		else { Game.println(this.getModulType() + " : " +Error.ERROR_INSUFFICIENT_CREDIT.showError()); }
		return false;
	}
	
	public boolean buyHead(Team tk)
	{
		// decrease costs 
		double price = getPrice();
		if(tk.getCredit() >= price){
			if(tk.addModule(this)){
				tk.setCredit(price,'d');
			return true;
			}
			else { Game.println(Error.ERROR_TEAM_MAX_MODULES.showError()); }
		}
		else { Game.println(this.getModulType() + " : " +Error.ERROR_INSUFFICIENT_CREDIT.showError()); }
		return false;
	}
	
	public boolean buyLeg(Team tk)
	{
		// decrease costs 
		double price = getPrice();
		if(tk.getCredit() >= price){
			if(tk.addModule(this)){
				tk.setCredit(price,'d');
			return true;
			}
			else { Game.println(Error.ERROR_TEAM_MAX_MODULES.showError()); }
		}
		else { Game.println(this.getModulType() + " : " +Error.ERROR_INSUFFICIENT_CREDIT.showError()); }
		return false;
	}
	
	public boolean buyArm(Team tk)
	{
		// decrease costs 
		double price = getPrice();
		if(tk.getCredit() >= price){
			if(tk.addModule(this)){
				tk.setCredit(price,'d');
			return true;
			}
			else { Game.println(Error.ERROR_TEAM_MAX_MODULES.showError()); }
		}
		else { Game.println(this.getModulType() + " : " +Error.ERROR_INSUFFICIENT_CREDIT.showError()); }
		return false;
	}
	
	public int Modul_point()
	{	// random numbers xD
		if(module_type.equals("hd")) return 10; 
		else if(module_type.equals("tr")) return 246;
		else if(module_type.equals("lg")) return 787;
		else if(module_type.equals("ar")) return 44;
		else return 0; 
	}
	
	public void getPower(Robot rb)
	{
		int leg_force = 0;
		switch(Modul_Type()){
		
		case 1: 
			rb.setWeight(20 + (module_level * 1)); 
			rb.setIntelligence(100 + (module_level * 160));
		break; // head
		case 2: 
			rb.setWeight(100 + ( module_level * 10));
			rb.setForce(100 + (module_level * 80));
			rb.torso_force = (100 + (module_level * 80));
		break; // torso
		case 3: 
			rb.setWeight(80 + (module_level*4));
			rb.setForce(100 + (module_level*80));
			leg_force = (100 + (module_level*80));
			rb.leg_force = leg_force;
			rb.setSpeed((250 * leg_force) / rb.getWeight());
		break; // leg
		case 4: 
			rb.setWeight(40 + (module_level*2));
			rb.setSkill(100 + (module_level*200));
		break; // arm
		}
		
	}
	
	public int getPrice()
	{
		if(module_type.equals("tr")) return TORSO_PRICE * module_level;
		else if(module_type.equals("hd")) return HEAD_PRICE * module_level;
		else if(module_type.equals("lg")) return LEG_PRICE * module_level;
		else if(module_type.equals("ar")) return ARM_PRICE * module_level;
		else return 0;
	}
}

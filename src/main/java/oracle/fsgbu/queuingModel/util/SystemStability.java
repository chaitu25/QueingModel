package oracle.fsgbu.queuingModel.util;

public enum SystemStability {
	Stable("Stable"), SemiStable("Semi-Stable"), Unstable("Unstable");

	String value;

	SystemStability(String value) {
		this.value = value;
	}
	
	public String getStability() {
		return value;
	}

}

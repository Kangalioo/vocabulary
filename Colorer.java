public class Colorer {
	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
	}
	
	public static String colored(String string, int color) {
		if (isWindows()) return string;
		return setColor(color) + string + setColor(-1);
	}
	
	public static String setColor(int color) {
		if (isWindows()) return "";
		if (color == -1) color = 39;
		else if (color < 8) color = 30 + color;
		else color = 90 + color - 8;
		
		return setAttribute(color);
	}
	
	public static String setAttribute(int number) {
		if (isWindows()) return "";
		return "\u001b[" + number + "m";
	}
}

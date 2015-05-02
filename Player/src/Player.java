import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;


public class Player {
	DBConnector dbc;
	String name;
	Scanner in = new Scanner(System.in);
	
	
	public Player(DBConnector dbc) {
		this.dbc = dbc;
		this.name = "";
	}
	
	/**
	 * Connects to the server and creates a new instance of Player.
	 */
	public static void main(String[] args) {	
		System.setProperty("java.security.policy", "security.policy");
		System.setSecurityManager(new RMISecurityManager());
	
		Registry targetReg;
		DBConnector dbc;
		try {
			targetReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
			dbc = (DBConnector) targetReg.lookup("dbc");

			Player player = new Player(dbc);
			System.out.println("WELCOME TO THE QUIZ GAME!");
			System.out.println();
			player.launch();
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Gets the name of the user and request him or her to select a task to be executed. 
	 * 
	 * @throws RemoteException
	 */
	public void launch() throws RemoteException {
		System.out.print("Please enter your name: ");
		String name = in.nextLine();
		displayOpts();
		
		int opt = 0;
		while(opt != 3 ) {
			opt = getInt();
			
			switch (opt) {
				case 1: getQuizList(); break;
				case 2: playQuiz(name); break;
				case 3: System.out.println(); break;
				default: System.out.println("Invalid option.");
			}
		}
		System.out.println("GOOD BYE!");
	}
	
	/**
	 * Gets all open quiz ids and names from the server and displays them to the user.
	 * 
	 * @throws RemoteException
	 */
	public void getQuizList() throws RemoteException {
		List<List<Object>> quizzes = dbc.getData("select QUIZ_ID, QUIZ_NAME from QUIZ where STATUS = 'open'");
		System.out.println();
		System.out.println("You can select from the following quizzes: ");
		for(List<Object> row: quizzes) {
			System.out.println(row.get(0) + ". " + row.get(1));
		}
		System.out.println();
		displayOpts();
	}

	/**
	 * Prompts the user to enter the id of the quiz he or she wants to play. The questions that belong
	 * to this quiz id are read out from the database and looped through. At each iteration all the 
	 * choices that belong to that question are read from the database and displayed. If the answer 
	 * was correct the score is increased by one. 
	 * 
	 * @param name of the player. 
	 * @throws RemoteException
	 */
	public void playQuiz(String name) throws RemoteException {
		List<List<Object>> qids = dbc.getData("select QUIZ_ID, STATUS from QUIZ");
		int opt = -1;
		int score = 0;
		
		System.out.println("Enter the number of the quiz you would like to play:");
		opt = getInt();
		
		//Validating the quiz id.
		boolean hasID = false;
		for(List<Object> ids: qids) {
			if((int) ids.get(0) == opt && (ids.get(1).equals("open"))) hasID = true;
		}
		
		if(hasID == false) {
			System.out.println("Quiz " + opt + " is not available.");
			System.out.println();
			displayOpts();
			return;
		}
		
		List<List<Object>> questions = dbc.getData("select QUESTION_ID, QUESTION, ANSWER from QUESTION where QUIZ_ID = " + opt);
		
		for(List<Object> row: questions) {
			System.out.println(row.get(1));
			List<List<Object>> choices = dbc.getData("select CHOICE from CHOICE where QUESTION_ID = " + row.get(0));
			for(List<Object> c: choices) {
				System.out.println(c.get(0));
			}
			int answer = getInt();
			if(answer == (int) row.get(2)) {
				score++;
			}
		}
		dbc.insertData("INSERT INTO quiz.attempt (ATT_ID, QUIZ_ID, USER_NAME, SCORE) VALUES (NULL," + opt + ",'" + name + "'," + score + ")");
		System.out.println("Score: " + score);
		displayOpts();
	}
	
	/**
	 * Displays the menu the user can choose from at start or after a task has been finished. 
	 */
	private void displayOpts() {
		System.out.println("What would you like to do?");
		System.out.println("1. Get a list of quizzes I can play.");
		System.out.println("2. Play a quiz.");
		System.out.println("3. Quit.");
	}
	
	/**
	 * Parses the user input into int, if it false to do so MIN_VALUE is return. 
	 * 
	 * @return user integer input. 
	 */
	private int getInt() {
		String str = in.nextLine();
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return Integer.MIN_VALUE;
		}
	}
}

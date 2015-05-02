import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SetUpClient {
	private DBConnector dbc;
	Scanner in = new Scanner(System.in);
	
	public SetUpClient(DBConnector dbc) {
		this.dbc = dbc;
	}
	
	/**
	 * Connects to the server and creates a new instance of SetUpClient.
	 */
	public static void main(String[] args) {
		System.setProperty("java.security.policy", "security.policy");
		System.setSecurityManager(new RMISecurityManager());
	
		Registry targetReg;
		DBConnector dbc;
		try {
			targetReg = LocateRegistry.getRegistry("127.0.0.1", 1099);
			dbc = (DBConnector) targetReg.lookup("dbc");

			SetUpClient admin = new SetUpClient(dbc);
			System.out.println("WELCOME TO THE QUIZ GAME!");
			admin.launch();
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
		System.out.print("\nEnter your name: ");
		String creator = in.nextLine();
		displayOpts();
		
		int opt = 0;
		while(opt != 3 ) {
			opt = getInt();
			switch (opt) {
				case 1: createQuiz(creator); break;
				case 2: closeQuiz(); break;
				case 3: System.out.println(); break;
				default: System.out.println("Invalid option.");
			}
		}
		System.out.println("GOOD BYE!");
	}
	
	/**
	 * Prompts the user to enter the quiz id he or she wants to close, validates if the 
	 * entered id exists and open, if so it sets the status to closed. 
	 * Displays the winner(s) at the game. 
	 * 
	 * @throws RemoteException
	 */
	public void closeQuiz() throws RemoteException {
		List<List<Object>> qids = dbc.getData("select QUIZ_ID, STATUS from QUIZ");
		System.out.print("The ID of the quiz: ");
		
		int id = getInt();	
		//Validating the quiz id.
		boolean hasID = false;
		for(List<Object> ids: qids) {
			if((int) ids.get(0) == id && (ids.get(1).equals("open"))) hasID = true;
		}
		
		if(hasID == false) {
			System.out.println("Quiz " + id + " is not available or already closed.");
			System.out.println();
			displayOpts();
			return;
		}
		
		List<List<Object>> attempts = dbc.getData("select USER_NAME, SCORE from ATTEMPT where QUIZ_ID = " + id 
												+ " AND SCORE = (select MAX(SCORE) from ATTEMPT)");
		
		if(attempts.size() > 1) {
			System.out.println("The winners with equal score are: ");
		} else {
			System.out.print("The winner is: ");
		}
		
		for(List<Object> player: attempts) {
			System.out.println(player.get(0));
		}
		
		dbc.insertData("UPDATE quiz.quiz SET STATUS = 'closed' WHERE quiz.QUIZ_ID = " + id);
		System.out.println("Query " + id + " has been closed.");
		System.out.println();
		displayOpts();
	}
	
	/**
	 * Asks for the name of the quiz being created, the user can add an arbitrary number of 
	 * questions and choices, the list of choices for a question can be finished by entering
	 * <end>, similarly after the last question the user need to input <end> to let the program
	 * know that there is no more question to be entered. All questions and choices are displayed
	 * and the user is asked if he or she wants to save the game. If it is saved the id number 
	 * is displayed. 
	 * 
	 * @param creator, the user who creates the new quiz. 
	 * @return the id of the new quiz game. 
	 * @throws RemoteException
	 */
	public int createQuiz(String creator) throws RemoteException {
		
		System.out.print("Name of the quiz: ");
		String quizName = in.nextLine();
		System.out.println();
		
		System.out.println("Add questions and choices (finish by <end>):");
		System.out.print("First question: ");
		
		List<Question> questions = getQuestion();
		
		System.out.println();
		//Prints out the quiz to for the admin to review before saving. 
		System.out.println(quizName);
		for(Question q: questions) {
			System.out.println("\t" + q.getQuestion());
			for(String c: q.getChoises()) {
				System.out.println("\t\t" + c);
			}
		}
		System.out.println("\nWould you like to save it?");
		String save = in.nextLine();
		if (save.equals("yes") || save.equals("y")) {
			return sendToDB(quizName, creator, questions);
		}
		return Integer.MIN_VALUE;
	}
	
	/**
	 * Builds insert statements that are sent to the server to save the data into the quiz, question 
	 * and choice tables.
	 *  
	 * @param quizName
	 * @param creator
	 * @param questions
	 * @return the id of the quiz. 
	 * @throws RemoteException
	 */
	private int sendToDB(String quizName, String creator, List<Question> questions) throws RemoteException {
		String queryQuestion = "INSERT INTO quiz.question (QUESTION_ID, QUESTION, QUIZ_ID, ANSWER) VALUES ";
		String queryChoice = "INSERT INTO quiz.choice (C_ID, CHOICE, QUESTION_ID) VALUES ";
		String queryQuiz = "";
		
		queryQuiz = "INSERT INTO quiz.quiz (QUIZ_ID, QUIZ_NAME, CREATOR) VALUES (NULL" + ",'" + quizName + "','" + creator + "')";
		int quizId = dbc.insertData(queryQuiz);

		for(Question q: questions) {
			queryQuestion = queryQuestion + ",(NULL" + ",'" + q.getQuestion() + "'," + quizId + "," + q.getAnswer() + ")";
			int qId = dbc.insertData(queryQuestion);

			for(String c: q.getChoises()) {
				queryChoice = queryChoice + ",(NULL" + ",'" + c + "'," + qId + ")";
			}
		}
		
		dbc.insertData(queryChoice + queryChoice.substring(1));
		
		System.out.println("The ID of this quiz is " + quizId + ".");
		System.out.println();
		displayOpts();
		return quizId;
	}
	
	/**
	 * Creates new question objects that are returned in a list to the createQuiz method. 
	 * 
	 * @return list of questions.
	 */
	private List<Question> getQuestion() {
		List<Question> questions = new ArrayList();
		String text = "";
		while(!text.equals("<end>")) {
			text = in.nextLine();
			if(!text.equals("<end>")) {
				List<String> choices = getChoice();
				System.out.print("The correct answers number: ");
				int answer = in.nextInt();
				questions.add(new Question(text, answer, choices));
				System.out.println();
				System.out.print("Next question: ");
				text = in.nextLine();
			}
		}
		return questions;
	}
	
	/**
	 * Creates a list of choices with their option number in a for a string and 
	 * returns them to the getQuestion method as a list of strings. 
	 * 
	 * @return list of choices as string. 
	 */
	private List<String> getChoice() {
		int i = 0;
		String text = "";
		List<String> choices = new ArrayList();
		while(!text.equals("<end>")) {
			i++;
			System.out.print(i + ": ");
			text = in.nextLine();
			if(!text.equals("<end>")) {
				choices.add(i + ". " + text);
			}
		}
		return choices;
	}
	
	/**
	 * Displays the menu the user can choose from at start or after a task has been finished. 
	 */
	private void displayOpts() {
		System.out.println("What would you like to do?");
		System.out.println("1. Create a new quiz.");
		System.out.println("2. Close a quiz game.");
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
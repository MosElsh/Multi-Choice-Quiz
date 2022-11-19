// This program is a multiple choice quiz game.

import java.util.Scanner;
import java.io.*;

// Stores data about a question (record).
class Question {
    String questionDisplayed;
    String[] possibleAnswers = new String[4];
    int[] pointsGained = new int[4];
} // END class Question

// Stores data about each person's score saved (record).
class Participant {
    String name;
    int score;
} // END class Participant

class Quiz
{
    public static void main(String[] args) throws IOException
    {
        quizProgram();
        return;
    }
    
    // Starts the quiz program.
    public static void quizProgram() throws IOException
    {
        mainMenu();
        playQuiz();
        endMessage();
        return;
    } // END quizProgram
    
    // Validates what the user enters in the main menu.
    public static void mainMenu() throws IOException
    {
        String menu_option = "";
        boolean valid = false;

        output("Welcome to the Multi-Choice Quiz Game.");
        mainMenuOptions();

        while (!valid) {

            menu_option = inputInfo("Please enter your option.");
            output("");
            if ("rules".equals(menu_option.toLowerCase())) {
                printRules();
                mainMenuOptions();
            }
            else if ("start".equals(menu_option.toLowerCase())) {
                valid = true; // Breaks out of while loop.
            }
            else if ("high scores".equals(menu_option.toLowerCase())) {
                displayScoresTable();
                mainMenuOptions();
            }
            else {
                output("This isn't a valid option.");
            }
        }

        output("");
        return;
    } // END mainMenu
    
    // Plays the quiz
    public static int playQuiz() throws IOException
    {
        int totalScore = 0;
        Question[] questionsArray = processQuestionArray();
        final int NUMBER_OF_QUESTIONS = questionsArray.length;

        output("The quiz has started. The first question is coming up soon.");
        output("");
        for (int count = 0; count < NUMBER_OF_QUESTIONS; count++) {
            output("Question Number " + (count + 1));
            totalScore += processQuestionResponse(questionsArray[count]);
        }
        output("Your total score is: " + totalScore + " points.");

        storeScoreDetails(totalScore);

        return totalScore;
    } // END playQuiz

    // Gathers the data from the scores file, creates Participant records and stores them in an array.
    public static Participant[] createScoresArray() throws IOException {

        // Gathers the number of lines in the file.
        BufferedReader scoresFileLines = new BufferedReader(new FileReader("highScores.csv"));
        final int NUMBER_OF_SCORES = (int)scoresFileLines.lines().count();
        scoresFileLines.close();

        Participant[] scoresArray = new Participant[NUMBER_OF_SCORES];

        BufferedReader scoresFile = new BufferedReader(new FileReader("highScores.csv"));

        for (int x = 0; x < NUMBER_OF_SCORES; x++) {
            String score = scoresFile.readLine();
            String[] splitData = score.split(",");
            scoresArray[x] = createParticipantRecord(splitData[0], Integer.parseInt(splitData[1]));
        }

        scoresFile.close();

        return scoresArray;
    } // END createScoresArray

    // Uses a bubble sort to sort an array in descending order.
    public static Participant[] sortScoresArrayDescending(Participant[] scoresArray) {
        for (int x = 0; x < scoresArray.length; x++) {
            for (int y = 0; y < scoresArray.length - (x+1); y++) {
                if (getParticipantScore(scoresArray[y]) < getParticipantScore(scoresArray[y+1])) {
                    Participant temp = scoresArray[y+1];
                    scoresArray[y+1] = scoresArray[y];
                    scoresArray[y] = temp;
                }
            }
        }

        return scoresArray;
    } // END sortScoresArrayDescending

    // Adds space between a minimum and maximum value.
    public static String extraSpace(int min, int max) {
        String spaces = "";
        for (int y = min; y < max; y++) {
            spaces = spaces + " ";
        }
        return spaces;
    } // END extraSpace

    // Displays a scores table in "{name} {score}" fashion.
    public static void displayScoresTable() throws IOException {
        final int MAX_NAME_CHARACTERS = 20;
        final int MAX_SCORE_DIGITS = 6;
        final String SEPARATOR = "|";

        Participant[] scoresArray = createScoresArray();
        Participant[] sortedScoresArray = sortScoresArrayDescending(scoresArray);

        output("Here are the scores in order:");

        output("");
        output("-----------------------------");

        for (int x = 0; x < sortedScoresArray.length; x++) {

            // Table cell with the name.
            String lineOutput = SEPARATOR;
            lineOutput = lineOutput + getParticipantName(sortedScoresArray[x]);
            lineOutput = lineOutput + extraSpace(getParticipantName(sortedScoresArray[x]).length(), MAX_NAME_CHARACTERS);
            lineOutput = lineOutput + SEPARATOR;

            // Table cell with the score.
            String stringScore = "" + getParticipantScore(sortedScoresArray[x]);
            lineOutput = lineOutput + stringScore;
            lineOutput = lineOutput + extraSpace(stringScore.length(), MAX_SCORE_DIGITS);
            lineOutput = lineOutput + SEPARATOR;

            output(lineOutput);
            if (!(x == sortedScoresArray.length - 1)) {
                output("|--------------------|------|");
            }
        }

        output("-----------------------------");
        output("");

        return;
    } // END displayScoresTable

    // Asks for the user's name and appends the score details in a file.
    public static void storeScoreDetails(int totalScore) throws IOException {
        String name = inputNames("Enter your name so that we can save your score.");

        PrintWriter highScores = new PrintWriter(new FileWriter("highScores.csv", true));
        highScores.println(name + "," + totalScore);
        highScores.close();

        output("Your score has been saved.");
        return;
    } // END storeScoreDetails
    
    // Display a question, give the user a chance to answer and return the score.
    public static int processQuestionResponse(Question question)
    {
        final int SCORES_INDEX_CORRECTION = -1;
        int quizScore = 0;
        int answer;

        printQuestion(getQuestionDisplayed(question), getPossibleAnswers(question));

        answer = validateAnswerInput();
        quizScore = getPointsGained(question)[answer + SCORES_INDEX_CORRECTION];

        output("Your score for this question is: " + quizScore + " points.");
        output("");

        return quizScore;
    } // END processQuestionResponse   
    
    // Checks that user input is between 1 and 4. Returns the user input.
    public static int validateAnswerInput()
    {
        int answer = 0;
        boolean valid = false; // Starts while loop running.

        while (!valid) {
            System.out.println("Please enter your answer below.");
            Scanner scanner = new Scanner(System.in);

            if (scanner.hasNextInt()) {
                answer = scanner.nextInt();
                if (answer < 1 || answer > 4) { // If answer is lower than 1 or greater than 4.
                    output("Your answer must be between 1 and 4.");
                }
                else {
                    valid = true; // Finish while loop.
                }
            }
            else {
                output("Your answer must be between 1 and 4.");
            }
        }

        return answer;
    } // END validateAnswerInput
    
    // Captures keyboard input from the user and returns the entered information.
    public static String inputInfo(String message)
    {
        String input;
        Scanner scanner = new Scanner(System.in);

        output(message);
        input = scanner.nextLine();

        return input;
    } // END inputInfo

    // Lets the user enter a name. Checks that tehre are no digits inside the entered name.
    public static String inputNames(String message) {
        boolean valid = false;
        String input = "";

        while (!valid) {
            input = inputInfo(message);
            if (checkInputAllLetters(input) && input.length() <= 20) {
                valid = true;
            }
            else if (input.length() > 20) {
                output("Names must less than 20 charatcers. Please use an abbreviation of names if possible.");
            }
            else {
                output("There must not be digits inside names. Please try again.");
            }
        }

        return input;
    } // END inputNames

    // Checks that all characters within a String are not digits. Returns a boolean of whether this is true or not.
    public static boolean checkInputAllLetters(String characters) {
        boolean flag = true;
        for (int x = 0; x < characters.length(); x++) {
            if (Character.isDigit(characters.charAt(x))) {
                flag = false;
            }
        }

        return flag;
    } // END checkInputAllLetters
    
    // Displays main menu options.
    public static void mainMenuOptions()
    {
        output("Enter 'Rules' if you would like to see the game's rules.");
        output("Enter 'Start' if you would like to play");
        output("Enter 'High Scores' if you would like to view the scores table.");

        return;
    } // END mainMenuOptions
    
    // Displays goodbye message
    public static void endMessage()
    {
        output("");
        output("Thank you for playing the Multi-Choice Quiz Game.");
        output("");
        return;
    } // END endMessage
    
    // Process the question display.
    public static void printQuestion(String questionDisplayed, String[] possibleAnswers) {
        output(questionDisplayed);
        for (int x = 0; x < possibleAnswers.length; x++) {
            output((x+1) + ") " + possibleAnswers[x]);
        }
        return;
    } // END printQuestion
    
    // Displays the rules to the user
    public static void printRules()
    {
        output("");
        output("These are the rules of the game:");
        output("");
        output("    - You must enter an answer for a question.");
        output("    - Each question has 4 options to choose from. Only one is the correct answer.");
        output("    - If you select the correct answer, you get 10 points.");
        output("    - If you select the second best answer, you get 5 points.");
        output("    - If you select the third best answer, you get 2 points.");
        output("    - If you select the worst answer, you get 0 points.");
        output("");
        output("Your points are added up throughout the quiz and are shown to you at the end of the quiz.");
        output("");

        return;
    } // END printRules
    
    // Takes in a message as a String parameter and displays the message to the user.
    public static void output(String message)
    {
        System.out.println(message);
        return;
    } // END output
    
    // Creates and returns a list of questions pre processed for the quiz.
    public static Question[] processQuestionArray() {
        final int CORRECT_ANSWER_POINTS = 10;
        final int SECOND_ANSWER_POINTS = 5;
        final int THIRD_ANSWER_POINTS = 2;
        final int FOURTH_ANSWER_POINTS = 0;
        final int NUMBER_OF_QUESTIONS = 5;

        Question[] questionsArray = new Question[NUMBER_OF_QUESTIONS];

        // Question 1
        String[] possibleAnswers = {"330 m/s", "343 m/s", "353 m/s", "360 m/s"};
        int[] pointsGained = {SECOND_ANSWER_POINTS, CORRECT_ANSWER_POINTS, THIRD_ANSWER_POINTS, FOURTH_ANSWER_POINTS};
        Question question1 = createQuestionRecord("What is the speed of sound?", possibleAnswers, pointsGained);

        // Question 2
        possibleAnswers = new String[]{"48", "54", "57", "60"};
        pointsGained = new int[]{FOURTH_ANSWER_POINTS, THIRD_ANSWER_POINTS, SECOND_ANSWER_POINTS, CORRECT_ANSWER_POINTS};
        Question question2 = createQuestionRecord("What is the most points that a darts player can score with a single throw?", possibleAnswers, pointsGained);

        // Question 3
        possibleAnswers = new String[]{"22", "23", "24", "25"};
        pointsGained = new int[]{FOURTH_ANSWER_POINTS, SECOND_ANSWER_POINTS, CORRECT_ANSWER_POINTS, THIRD_ANSWER_POINTS};
        Question question3 = createQuestionRecord("How many time zones are there in total?", possibleAnswers, pointsGained);

        // Question 4
        possibleAnswers = new String[]{"5", "6", "7", "8"};
        pointsGained = new int[]{FOURTH_ANSWER_POINTS, THIRD_ANSWER_POINTS, CORRECT_ANSWER_POINTS, SECOND_ANSWER_POINTS};
        Question question4 = createQuestionRecord("How many colours are there in a rainbow?", possibleAnswers, pointsGained);

        // Question 5
        possibleAnswers = new String[]{"30", "31", "32", "33"};
        pointsGained = new int[]{FOURTH_ANSWER_POINTS, THIRD_ANSWER_POINTS, SECOND_ANSWER_POINTS, CORRECT_ANSWER_POINTS};
        Question question5 = createQuestionRecord("How many letters are there in the Russian alphabet?", possibleAnswers, pointsGained);

        questionsArray[0] = question1;
        questionsArray[1] = question2;
        questionsArray[2] = question3;
        questionsArray[3] = question4;
        questionsArray[4] = question5;

        return questionsArray;
    } // END processQuestionArray
    
    // Creates a Question record using the data its given and its accessor methods.
    public static Question createQuestionRecord(String questionDisplayed, String[] possibleAnswers, int[] pointsGained) {
        Question question = new Question();
        question = setQuestionDisplayed(question, questionDisplayed);
        question = setPossibleAnswers(question, possibleAnswers);
        question = setPointsGained(question, pointsGained);

        return question;
    } // END createQuestionRecord

    // Sets the question displayed as a String to the questionDisplayed field of the Question record.
    public static Question setQuestionDisplayed(Question q, String questionDisplayed) {
        q.questionDisplayed = questionDisplayed;
        return q;
    } // END setQuestionDisplayed

    // Sets the possible answers array to the possibleAnswers field of the Question record.
    public static Question setPossibleAnswers(Question q, String[] possibleAnswers) {
        q.possibleAnswers = possibleAnswers;
        return q;
    } // END setPossibleAnswers

    // Sets the points gained array to the pointsGained field of the Question record.
    public static Question setPointsGained(Question q, int[] pointsGained) {
        q.pointsGained = pointsGained;
        return q;
    } // END setPointsGained

    // Returns the questionDisplayed field of the Question record.
    public static String getQuestionDisplayed(Question q) {
        return q.questionDisplayed;
    } // END getQuestionDisplayed

    // Returns the possibleAnswers array field of the Question record.
    public static String[] getPossibleAnswers(Question q) {
        return q.possibleAnswers;
    } // END getPossibleAnswers

    // Returns the pointsGained array field of the Queston record.
    public static int[] getPointsGained(Question q) {
        return q.pointsGained;
    } // END getPointsGained;

    // Sets the name of the participant saved in the CSV file as a String in the Participant reocrd.
    public static Participant setParticipantName(Participant p, String name) {
        p.name = name;
        return p;
    } // END sepParticipantName

    // Sets the score of the participant saved in the CSV file as an ineger in the Participant record.
    public static Participant setParticipantScore(Participant p, int score) {
        p.score = score;
        return p;
    } // END setParticipantScore

    // Returns the name of the participant from a given Participant record.
    public static String getParticipantName(Participant p) {
        return p.name;
    } // END getParticipantName

    // Returns the score of the participant from a given Participant record.
    public static int getParticipantScore(Participant p) {
        return p.score;
    } // END getParticipantScore

    // Create a Participant record using a given name as a String and a given score as an integer.
    public static Participant createParticipantRecord(String name, int score) {
        Participant p = new Participant();
        p = setParticipantName(p, name);
        p = setParticipantScore(p, score);
        return p;
    } // END createParticipantRecord
}
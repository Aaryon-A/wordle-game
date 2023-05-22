import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The goal of this program is to recreate the popular word guessing game: Wordle
 * I have succesfully implemented all of the features of the game and added some of my own such
 * as a start screen and a help screen
 * Hope you enjoy playing!
 * 
 * @author Aaryon Arora
 * @version 1.0
 * @since 2022-11-16
 */
public class Wordle extends Application {

    /**
     * 2D list of the Rectangle object which holds all of the squares
     */
    Rectangle[][] square;
    /**
     * 2D list of the Text object which holds all of the letters
     */
    Text[][] text;
    /**
     * List of the HBox object which holds each row of squares
     */
    HBox[] squares;
    /**
     * List of the HBox object which holds each row of letters
     */
    HBox[] texts;
    /**
     * List of the StackPane object which overlaps the letters on top of the squares
     */
    StackPane[] panes;
    /**
     * List of the Text object which is used to store each letter of the alphabet which indicates what letter is in the Wordle word
     */
    Text[] letter;
    /**
     * ArrayList of every letter of the alphabet
     */
    ArrayList<String> alphabet;

    /**
     * Used to store which row of squares/text the user is on
     */
    int row;
    /**
     * Used to store which collumn of squares/text the user is on
     */
    int collumn;

    /**
     * Used to store the word the user has to guess
     */
    String chosenWord;
    /**
     * Used to see how many letters the user has guessed right each guess
     */
    int greenLetters;
    /**
     * Is set to true when the user has guessed the word
     */
    boolean win;
    /**
     * Button used to restart the game
     */
    Button restartButton;

    /**
     * ArrayList of every english word in the human language
     */
    ArrayList<String> englishWords;
    /**
     * ArrayList of every possible word that can be used in the game
     */
    ArrayList<String> wordleWords;

    /**
     * VBox that holds all of the game objects
     */
    VBox gamePane;
    /**
     * VBox that holds all of the objects for the start screen
     */
    VBox startPane;
    /**
     * VBox that holds all of the objects for the help screen
     */
    VBox helpPane;

    /**
     * Set to true when the user wants dark mode
     */
    boolean darkMode = false;

    /**
     * Function that runs at the start of the program
     * Initializes all variables
     */
    @Override
    public void init() {
        // Initializes all lists
        square = new Rectangle[5][6];
        text = new Text[5][6];
        squares = new HBox[6];
        texts = new HBox[6];
        panes = new StackPane[6];
        letter = new Text[26];
        alphabet = new ArrayList<>();

        row = 0;
        collumn = 0;

        greenLetters = 0;
        win = false;

        englishWords = new ArrayList<>();
        wordleWords = new ArrayList<>();

        gamePane = new VBox();
        startPane = new VBox();
        helpPane = new VBox();

        // Reads the text file of all english words and adds the 5 letter ones to the list
        try (BufferedReader br = new BufferedReader(new FileReader("src\\main\\java\\words.txt"))) {
            for (String line; (line = br.readLine()) != null;) {
                if (line.length() == 5) {
                    englishWords.add(line);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        // Reads the list of all wordle words and adds them to the list
        try (BufferedReader br = new BufferedReader(new FileReader("src\\main\\java\\wordleWords.txt"))) {
            for (String line; (line = br.readLine()) != null;) {
                wordleWords.add(line);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        // Randomly chooses a word from the list to be the chosen word
        Random rand = new Random();
        chosenWord = wordleWords.get(rand.nextInt(wordleWords.size()));
        System.out.println(chosenWord);
    }

    /**
     * Function that call all other functions and displays everything on the screen
     * 
     * @param stage the main display where everything is shown
     */
    @Override
    public void start(Stage stage) {
        // Initializes the start screen
        startScreen(stage, startPane);

        // Makes a new Scene object for each screen
        Scene gameScene = new Scene(gamePane, 500, 600);

        Scene startScene = new Scene(startPane, 500, 600);

        Scene helpScene = new Scene(helpPane, 500, 600);


        // Sets the stage to be the start screen and makes sure it isn't resizeable
        stage.setScene(startScene);
        stage.show();
        stage.setResizable(false);
        stage.setTitle("Wordle");

        // Handles the logic for when a key is pressed in the game screen
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            /**
             * Runs when a key is pressed
             * 
             * @param event the key that was pressed
             */
            @Override
            public void handle(KeyEvent event) {
                // Runs the function that handles inputting text
                enterText(event, stage, gamePane);
                // If the escape key is pressed then change the screen to the start screen
                if(event.getCode() == KeyCode.ESCAPE) {
                    // Resets all variables and initializes the screen
                    init();
                    startScreen(stage, startPane);
                    startScene.setRoot(startPane);
                    stage.setScene(startScene);
                }
            }
        });

        // Handles the logic for when a key is pressed in the start screen
        startScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            /**
             * Runs when a key is pressed
             * 
             * @param event the key that was pressed
             */
            @Override
            public void handle(KeyEvent event) {
                // Changes the screen to the game screen
                 if(event.getCode() == KeyCode.ENTER) {
                    init();
                    gameScreen(stage, gamePane);
                    gameScene.setRoot(gamePane);
                    stage.setScene(gameScene);
                 }   
            }

        });

        // Handles the logic for when the mouse is clicked
        startScene.setOnMouseClicked(new EventHandler<MouseEvent>() {

            /**
             * Runs when the mouse is clicked
             * 
             * @param event the information about the mouse
             */
            @Override
            public void handle(MouseEvent event) {
                // If the mouse was clicked in the box that is being used as a button then go to the help screen
                if(event.getX() > 152 && event.getX() < 347 && event.getY() > 418 && event.getY() < 462) {
                    init();
                    helpScreen(stage, helpPane);
                    helpScene.setRoot(helpPane);
                    stage.setScene(helpScene);
                }             
            }

        });

        // Handles the logic for when the mouse is clicked
        helpScene.setOnMouseClicked(new EventHandler<MouseEvent>() {

            /**
             * Runs when the mouse is clicked
             * 
             * @param event the information about the mouse
             */
            @Override
            public void handle(MouseEvent event) {
                // If the mouse was clicked in the box then go to the start screen
                if(event.getX() > 154 && event.getX() < 342 && event.getY() > 468 && event.getY() < 512) {
                    init();
                    startScreen(stage, startPane);
                    startScene.setRoot(startPane);
                    stage.setScene(startScene);
                }
            }

        });
    }

    /**
     * Initializes the help screen
     * 
     * @param stage the stage used to display everything
     * @param rootPane the VBox that holds all of the objects
     */
    private void helpScreen(Stage stage, VBox rootPane) {
        // Creates some text and boxes
        if(darkMode) {
            rootPane.setBackground(Background.fill(Paint.valueOf("#808080")));
        }
        Text helpText = new Text("Instructions");
        helpText.setFont(new Font("Times new Roman", 40));
        if(darkMode) {
            helpText.setFill(Paint.valueOf("White"));
        }

        rootPane.setAlignment(Pos.CENTER);
        rootPane.getChildren().add(helpText);

        Rectangle backButton = new Rectangle();
        backButton.setWidth(200);
        backButton.setHeight(50);
        backButton.setArcWidth(20);
        backButton.setArcHeight(20);
        if(darkMode) {
            backButton.setFill(Paint.valueOf("White"));
        }
    
        Text backText = new Text("Click here to go Back");
        backText.setFont(new Font("Times New Roman", 20));
        backText.setFill(Paint.valueOf("White"));
        if(darkMode) {
            backText.setFill(Paint.valueOf("Black"));
        }

        // Creates a set of text and loops through it to make it a certain font and add it to the screen
        Text[] instructions = new Text[8];

        instructions[0] = new Text("The goal of this game is to guess the word in 6 tries");
        instructions[1] = new Text("You can only guess 5 letter words");
        instructions[2] = new Text("When you guess a word a green square means the letter is in the right spot");
        instructions[3] = new Text("A purple square means the letter is in the wrong spot");
        instructions[4] = new Text("A grey square means the letter is not in the word");
        instructions[5] = new Text("If you fail or get it right you can always restart");
        instructions[6] = new Text("Click the escape key to go back to the start screen once you start playing");
        instructions[7] = new Text("Good Luck and Hope You Have Fun!!!");

        for(int i = 0; i < instructions.length; i++) {
            instructions[i].setFont(new Font("Times new Roman", 15));
            if(darkMode) {
                instructions[i].setFill(Paint.valueOf("White"));
            }
            rootPane.getChildren().add(instructions[i]);
        }

        // Overlaps the text and the rectangle to make a button
        StackPane backPane = new StackPane();
        backPane.setAlignment(Pos.CENTER);
        backPane.getChildren().addAll(backButton, backText);
        backPane.setTranslateY(100);
        backPane.translateYProperty();
        rootPane.getChildren().add(backPane);
    }

    /**
     * Initializes the start screen
     * 
     * @param stage the main display that shows everything
     * @param rootPane the VBox that holds everything
     */
    private void startScreen(Stage stage, VBox rootPane) {
        // Creates all of the text and buttons
        rootPane.setAlignment(Pos.CENTER);

        if(darkMode) {
            rootPane.setBackground(Background.fill(Paint.valueOf("#808080")));
        }

        Text wordleText = new Text("Wordle");
        wordleText.setFont(new Font("Times New Roman", 40));
        if(darkMode) {
            wordleText.setFill(Paint.valueOf("White"));
        }
        rootPane.getChildren().add(wordleText);

        Text startText = new Text("Click Enter to Start");
        startText.setFont(new Font("Times new Roman", 30));
        if(darkMode) {
            startText.setFill(Paint.valueOf("White"));
        }
        rootPane.getChildren().add(startText);


        Rectangle helpButton = new Rectangle();
        helpButton.setWidth(200);
        helpButton.setHeight(50);
        helpButton.setArcWidth(20);
        helpButton.setArcHeight(20);
        if(darkMode) {
            helpButton.setFill(Paint.valueOf("White"));
        }
    
        Text helpText = new Text("Click here for Help");
        helpText.setFont(new Font("Times New Roman", 20));
        helpText.setFill(Paint.valueOf("White"));
        if(darkMode) {
            helpText.setFill(Paint.valueOf("Black"));
        }

        StackPane helpPane = new StackPane();
        helpPane.setAlignment(Pos.CENTER);
        helpPane.getChildren().addAll(helpButton, helpText);
        helpPane.setTranslateY(100);
        helpPane.translateYProperty();
        rootPane.getChildren().add(helpPane);

        Button darkModeButton = new Button("Dark Mode");
        // Makes sure clicking the enter key starts the game and doesn't click the button
        darkModeButton.setFocusTraversable(false);
        darkModeButton.setOnAction(new EventHandler<ActionEvent>() {

            /**
             * Runs when the dark mode button is clicked
             * 
             * @param event the type of action event
             */
            @Override
            public void handle(ActionEvent event) {
                darkMode = !darkMode;
                init();
                start(stage);         
            }
            
        });
        rootPane.getChildren().add(darkModeButton);
    }

    /**
     * Initializes the game screen
     * 
     * @param stage the main display
     * @param rootPane the VBox that holds every object
     */
    private void gameScreen(Stage stage, VBox rootPane) {
        rootPane.setAlignment(Pos.CENTER);
        if(darkMode) {
            rootPane.setBackground(Background.fill(Paint.valueOf("#808080")));
        }
        

        Text wordleText = new Text("Wordle");
        wordleText.setFont(new Font("Times New Roman", 40));
        rootPane.getChildren().add(wordleText);

        // Loops through every letter of the alphabet and adds it to a list of letters and a list of text
        // The list of letters is used to easily find the position of each letter since the arraylist has a built-in function
        int increment = 0;
        HBox letters = new HBox();
        letters.setSpacing(5);
        letters.setAlignment(Pos.CENTER);
        for (char temp = 'A'; temp <= 'Z'; temp++) {
            alphabet.add(String.valueOf(temp));
            letter[increment] = new Text();
            letter[increment].setText(String.valueOf(temp));
            letter[increment].setFont(new Font("Times New Roman", 15));
            letter[increment].setFill(Paint.valueOf("Black"));
            if(darkMode) {
                letter[increment].setFill(Paint.valueOf("White"));
            }

            letters.getChildren().add(letter[increment]);
            increment++;
        }

        rootPane.getChildren().add(letters);

        // Draws a line for aesthetics
        Line topLine = new Line();
        topLine.setStartX(0);
        topLine.setStartY(570);
        topLine.setEndX(500);
        topLine.setEndY(570);
        rootPane.getChildren().add(topLine);

        // Creates new HBoxes for each position
        for (int i = 0; i < squares.length; i++) {
            squares[i] = new HBox();
            squares[i].setAlignment(Pos.CENTER_LEFT);
            squares[i].setSpacing(10);
        }

        // Loops through the 2D list and makes a rectangle at each position
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 6; y++) {
                square[x][y] = new Rectangle();
                square[x][y].setWidth(50);
                square[x][y].setHeight(50);
                square[x][y].setFill(Paint.valueOf("Grey"));
                if(darkMode) {
                    square[x][y].setFill(Paint.valueOf("Black"));
                }
                squares[y].getChildren().add(square[x][y]);
            }
        }

        // Creates new HBoxes for each position
        for (int i = 0; i < texts.length; i++) {
            texts[i] = new HBox();
            texts[i].setAlignment(Pos.CENTER_LEFT);
            texts[i].setSpacing(10);
        }

        // Loops through the 2D list and makes a new Text object at each position
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 6; y++) {
                text[x][y] = new Text("");
                text[x][y].setFill(Paint.valueOf("White"));
                text[x][y].setFont(new Font("Times New Roman", 32));
                text[x][y].setTranslateX(10 + (x * 30));
                text[x][y].translateXProperty();

                texts[y].getChildren().add(text[x][y]);
            }
        }

        // Creates a new StackPane for each postion
        for (int i = 0; i < panes.length; i++) {
            panes[i] = new StackPane();
            panes[i].getChildren().addAll(squares[i], texts[i]);
            panes[i].setPadding(new Insets(10));
            panes[i].setTranslateX(100);
            panes[i].translateXProperty();
            rootPane.getChildren().add(panes[i]);
        }

        // Adds a bottom line for aesthetics
        Line bottomLine = new Line();
        bottomLine.setStartX(0);
        bottomLine.setStartY(570);
        bottomLine.setEndX(500);
        bottomLine.setEndY(570);
        rootPane.getChildren().add(bottomLine);
    }

    /**
     * Code used to display text typed by the user
     * 
     * @param event the key pressed
     * @param stage the main display
     * @param rootPane the VBox that holds every object
     */
    private void enterText(KeyEvent event, Stage stage, VBox rootPane) {
        // Stops inputting text if the user has won
        if (!win) {
            // Checks if the user has inputted a letter to make it easier to catch errors
            if (event.getCode().isLetterKey()) {
                try {
                    // The current text box is set to that letter
                    if (text[collumn][row].getText().equals("")) {
                        text[collumn][row].setText(event.getCode().toString());
                        // Credit to: https://www.tutorialspoint.com/javafx/javafx_animations.htm
                        ScaleTransition transition = new ScaleTransition();
                        transition.setDuration(Duration.millis(100));
                        transition.setNode(square[collumn][row]);
                        transition.setByX(0.5);
                        transition.setByY(0.5);
                        transition.setCycleCount(2);
                        transition.setAutoReverse(true);
                        transition.play();
                    }

                    // Increments collumn after each text to transition across
                    if (collumn < 5) {
                        collumn++;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }
            // If enter is clicked and the word typed exists in the english language
            if (event.getCode() == KeyCode.ENTER && collumn >= 5 && englishWords.contains(wordTyped(collumn, row))) {
                // Play a small animation of rotating the squares
                for(int i = 0; i < 5; i++) {
                    RotateTransition rotate = new RotateTransition();
                    rotate.setNode(square[i][row]);
                    rotate.setDuration(Duration.millis(100));
                    rotate.setCycleCount(2);
                    rotate.setByAngle(360);
                    rotate.play();
                }
                
                // Chnages the colour of each rectangle
                changeColor();
                // Moves down a row and resets the collumn
                row++;
                collumn = 0;
                // After 6 guesses the lose text appears
                if (row >= 6 && !win) {
                    // Prints the wordle
                    Text loseText = new Text("The Wordle was " + chosenWord);
                    loseText.setFont(new Font("Times New Roman", 40));
                    rootPane.getChildren().add(loseText);

                    // Displays the restart button
                    restartButton = new Button("Restart");
                    restartButton.setOnAction(new EventHandler<ActionEvent>() {

                        /**
                         * Runs when the button has been clicked
                         * 
                         * @param event the event that occured with the button
                         */
                        @Override
                        public void handle(ActionEvent event) {
                            restartApplication(stage);
                        }

                    });
                    rootPane.getChildren().add(restartButton);
                }

                if (win) {
                    // If the user got the word then display the win text
                    Text winText = new Text("You got the word!");
                    winText.setFont(new Font("Times New Roman", 40));
                    rootPane.getChildren().add(winText);

                    // Display the restart button as well
                    restartButton = new Button("Restart");
                    restartButton.setOnAction(new EventHandler<ActionEvent>() {

                        /**
                         * Runs when the button has been clicked
                         * 
                         * @param event the event that occured with the button
                         */
                        @Override
                        public void handle(ActionEvent event) {
                            restartApplication(stage);
                        }

                    });
                    rootPane.getChildren().add(restartButton);
                }
            }
            // If the backspace key is clicked then clear the text and go back a collumn
            if (event.getCode() == KeyCode.BACK_SPACE) {
                if (collumn == 4 && !(text[collumn][row].getText().equals(""))) {
                    // If the user is on the last collumn then just clear the text
                    text[collumn][row].setText("");
                } else if (collumn > 0) {
                    // Otherwise just go back one and clear the text
                    collumn--;
                    text[collumn][row].setText("");
                }

            }

        }
    }

    /**
     * Returns a string of each letter in the row of text
     * 
     * @param collumn collumn number that is selected
     * @param row row number that is selected
     * @return String the word that has been entered
     */
    private String wordTyped(int collumn, int row) {
        String word = text[collumn - 5][row].getText() + text[collumn - 4][row].getText()
                + text[collumn - 3][row].getText() + text[collumn - 2][row].getText()
                + text[collumn - 1][row].getText();

        word = word.toLowerCase();
        return word;
    }

    /**
     * Function that changes the colour of each rectangle when the enter key is clicked
     * Got the idea for the logic behind the colour change here: https://stackoverflow.com/questions/71617350/duplicate-letters-in-wordle
     */
    private void changeColor() {
        // Makes each letter uppercase and splits each letter into a different position in a list
        chosenWord = chosenWord.toUpperCase();
        String[] chosenLetters = chosenWord.split("");

        // Make every tile dark grey to show a guess has been made regardless of right letters
        for (int x = 0; x < 5; x++) {
            square[x][row].setFill(Paint.valueOf("darkgrey"));
            letter[alphabet.indexOf(text[x][row].getText())].setFill(Paint.valueOf("darkgrey"));
        }

        // First loop through each letter and if letter in the typed word is the same position as the one in the chosenWord then make it green
        for(int x = 0; x < 5; x++) {
            if(text[x][row].getText().equals(chosenLetters[x])) {
                square[x][row].setFill(Paint.valueOf("Green"));
                // Make the letter at the top of the screen the same colour as well
                letter[alphabet.indexOf(text[x][row].getText())].setFill(Paint.valueOf("Green"));
                // Remove the letter from the letters to avoid repeats
                chosenLetters[x] = "-";
                greenLetters++;
            }
        }

        // Now check if the letters remaining are inside of the word typed
        // They will have to be in the wrong position since we already checked for the right position
        for(int x = 0; x < 5; x++) {
            //if(!chosenLetters[x].equals("-")) {
                if(Arrays.asList(chosenLetters).contains(text[x][row].getText())) {
                    square[x][row].setFill(Paint.valueOf("mediumorchid"));
                    letter[alphabet.indexOf(text[x][row].getText())].setFill(Paint.valueOf("mediumorchid"));
                    chosenLetters[Arrays.asList(chosenLetters).indexOf(text[x][row].getText())] = "-";
                }
            //}   
        }

        // Once all 5 letters are green then set win to true
        if (greenLetters >= 5) {
            win = true;
            greenLetters = 0;
        }

        greenLetters = 0;
    }

    /**
     * Used when the restart button is clicked and resets all variables
     * 
     * @param stage main display
     */
    private void restartApplication(Stage stage) {
        init();
        start(stage);
    }

    /**
     * Main method that runs the program
     * 
     * @param args list of arguments
     */
    public static void main(String[] args) {
        launch();
    }

}
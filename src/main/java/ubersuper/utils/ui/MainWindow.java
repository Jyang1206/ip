package ubersuper.utils.ui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ubersuper.UberSuper;
import ubersuper.clients.ClientList;
import ubersuper.utils.LoadedResult;

import java.time.LocalDateTime;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private AnchorPane root;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextArea userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ToggleButton themeToggle;

    private boolean isDark = false;

    private UberSuper uberSuper;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private final Image uberSuperImage = new Image(this.getClass().getResourceAsStream("/images/Uber.png"));

    /**
     * Initializes UI behavior after FXML injection.
     * <p>
     * Binds auto-scroll of the {@link #scrollPane} to the dialog list height, wires keyboard
     * shortcuts for {@link #userInput} (Enter to send, Shift/Ctrl+Enter for newline),
     * ensures the theme toggle is present, and applies the initial theme.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Keyboard shortcuts: Enter to send, Shift+Enter for newline
        userInput.setOnKeyPressed(event -> {
            switch (event.getCode()) {
            case ENTER:
                if (!event.isShiftDown() && !event.isControlDown()) {
                    event.consume();
                    handleUserInput();
                }
                break;
            default:
                break;
            }
        });
        assert themeToggle != null : "themeToggle not injected — check fx:id and fx:controller";
        // Initialize theme text and class
        themeToggle.setText(isDark ? "Light" : "Dark");
        applyTheme(isDark);
    }

    /**
     * Injects the application core and shows the initial greeting from the bot.
     *
     * @param us the {@link UberSuper} instance that generates responses
     */
    public void setUberSuper(UberSuper us) {
        uberSuper = us;
        Node node = DialogBox.getUberDialog(uberSuper.greet(), uberSuperImage);
        dialogContainer.getChildren().add(node);
        fadeIn(node);
    }

    /**
     * Handles sending a message from the input field.
     * <p>
     * Appends a user dialog and the bot reply to {@link #dialogContainer},
     * animates them in, and clears {@link #userInput}.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = uberSuper.getResponse(input);
        Node userNode = DialogBox.getUserDialog(input, userImage);
        Node uberNode = DialogBox.getUberDialog(response, uberSuperImage);
        dialogContainer.getChildren().addAll(userNode, uberNode);
        fadeIn(userNode);
        fadeIn(uberNode);
        userInput.clear();
    }

    /**
     * Plays a brief fade-in animation for the provided node.
     *
     * @param node the UI element to animate
     */
    private void fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(180), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /**
     * Toggles between light and dark themes from the UI button.
     * Updates the toggle label and applies the selected theme.
     */
    @FXML
    public void handleChangeMode() {
        isDark = !isDark;
        themeToggle.setText(isDark ? "Light" : "Dark");
        applyTheme(isDark);
    }

    /**
     * <p>
     * AI-assisted code
     * Tried to implement light-dark mode similar to Orbital (React, JSX) utilized AI to understand FXML
     * </p>
     * Applies the selected theme by replacing the root container's stylesheets.
     * <p>
     * Uses the FXML root {@link #root} (falls back to the controller node) so this works
     * even before the control is attached to a {@link Scene}.
     *
     * @param dark whether to apply the dark theme (true) or light theme (false)
     */
    private void applyTheme(boolean dark) {
        String mainCss = getClass().getResource("/css/main.css").toExternalForm();
        String themeLight = getClass().getResource("/css/theme-light.css").toExternalForm();
        String themeDark = getClass().getResource("/css/theme-dark.css").toExternalForm();
        var sheets = root != null ? root.getStylesheets() : this.getStylesheets();
        sheets.setAll(mainCss, dark ? themeDark : themeLight);
    }

}

package JavaProject.Controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Animation {
    void playAnimation(AnchorPane anchorPane) {
        AnchorPane anchorPane1 = (AnchorPane) anchorPane.getChildren().get(1);
        AnchorPane anchorPane2 = (AnchorPane) anchorPane1.getChildren().get(1);
        StackPane stackPane = (StackPane) anchorPane1.getChildren().get(0);
        AnchorPane registerPane = (AnchorPane) stackPane.getChildren().get(0);
        AnchorPane loginPane = (AnchorPane) stackPane.getChildren().get(1);

        ParallelTransition parallelTransition = new ParallelTransition();

        TranslateTransition transition = new TranslateTransition();
        TranslateTransition transition2 = new TranslateTransition();
        TranslateTransition transition3 = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition2.setDuration(Duration.seconds(0.5));
        transition3.setDuration(Duration.seconds(0.5));
        transition.setToX(480);
        transition2.setToX(-205);
        transition3.setToX(-410);
        transition.setNode(anchorPane2);
        transition2.setNode(stackPane);
        transition3.setNode(stackPane);
        transition2.setOnFinished(e -> {
            ((AnchorPane) stackPane.getChildren().get(0)).setOpacity(1);
            ((AnchorPane) stackPane.getChildren().get(1)).setOpacity(0);
            ((AnchorPane) stackPane.getChildren().get(0)).toFront();
            transition3.play();
        });

        Button button = (Button) ((VBox) anchorPane2.getChildren().get(1)).getChildren().get(2);
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.seconds(0.5));
        scaleTransition.setToX(1.5);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.setNode(button);

        ScaleTransition scaleTransition2 = new ScaleTransition();
        scaleTransition2.setDuration(Duration.seconds(0.5));
        scaleTransition2.setToX(1.5);
        scaleTransition2.setAutoReverse(true);
        scaleTransition2.setCycleCount(2);
        scaleTransition2.setNode(anchorPane2);

        Text text = (Text) ((VBox) anchorPane2.getChildren().get(1)).getChildren().get(1);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.seconds(0.5));
        fadeTransition.setToValue(1);
        fadeTransition.setNode(text);

        FadeTransition fadeTransition1 = new FadeTransition();
        fadeTransition1.setDuration(Duration.seconds(0.5));
        fadeTransition1.setToValue(0);
        fadeTransition1.setNode(text);
        fadeTransition1.setOnFinished(e -> {
            text.setText("login to continue");
            fadeTransition.play();
        });

        parallelTransition.getChildren().addAll(transition, transition2, scaleTransition, scaleTransition2, fadeTransition1);
        parallelTransition.play();
        parallelTransition.setOnFinished(e -> {
            button.setText("Register");
            button.setOnAction(e1 -> playReverseAnimation(anchorPane));
        });
    }

    private void playReverseAnimation(AnchorPane anchorPane) {
        AnchorPane anchorPane1 = (AnchorPane) anchorPane.getChildren().get(1);
        AnchorPane anchorPane2 = (AnchorPane) anchorPane1.getChildren().get(1);
        StackPane stackPane = (StackPane) anchorPane1.getChildren().get(0);
        AnchorPane registerPane = (AnchorPane) stackPane.getChildren().get(0);
        AnchorPane loginPane = (AnchorPane) stackPane.getChildren().get(1);

        ParallelTransition parallelTransition = new ParallelTransition();

        TranslateTransition transition = new TranslateTransition();
        TranslateTransition transition2 = new TranslateTransition();
        TranslateTransition transition3 = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition2.setDuration(Duration.seconds(0.5));
        transition3.setDuration(Duration.seconds(0.5));
        transition.setToX(0);
        transition2.setToX(-205);
        transition3.setToX(0);
        transition.setNode(anchorPane2);
        transition2.setNode(stackPane);
        transition3.setNode(stackPane);
        transition2.setOnFinished(e -> {
            ((AnchorPane) stackPane.getChildren().get(0)).setOpacity(1);
            ((AnchorPane) stackPane.getChildren().get(1)).setOpacity(0);
            ((AnchorPane) stackPane.getChildren().get(0)).toFront();
            transition3.play();
        });

        Button button = (Button) ((VBox) anchorPane2.getChildren().get(1)).getChildren().get(2);
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.seconds(0.5));
        scaleTransition.setToX(1.5);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.setNode(button);

        ScaleTransition scaleTransition2 = new ScaleTransition();
        scaleTransition2.setDuration(Duration.seconds(0.5));
        scaleTransition2.setToX(1.5);
        scaleTransition2.setAutoReverse(true);
        scaleTransition2.setCycleCount(2);
        scaleTransition2.setNode(anchorPane2);

        Text text = (Text) ((VBox) anchorPane2.getChildren().get(1)).getChildren().get(1);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.seconds(0.5));
        fadeTransition.setToValue(1);
        fadeTransition.setNode(text);

        FadeTransition fadeTransition1 = new FadeTransition();
        fadeTransition1.setDuration(Duration.seconds(0.5));
        fadeTransition1.setToValue(0);
        fadeTransition1.setNode(text);
        fadeTransition1.setOnFinished(e -> {
            text.setText("welcome to our market");
            fadeTransition.play();
        });

        parallelTransition.getChildren().addAll(transition, transition2, scaleTransition, scaleTransition2, fadeTransition1);
        parallelTransition.play();
        parallelTransition.setOnFinished(e -> {
            button.setText("Login");
            button.setOnAction(e1 -> playAnimation(anchorPane));
        });
    }
}

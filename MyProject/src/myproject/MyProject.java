package myproject;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MyProject extends Application {

    Connection con;
    PreparedStatement prepare;
    ResultSet result;
    String ans;
    
    int audio_playing=-1;
    int audio_selected;
    int songs=0;
   
    String tableName;
    String username;
    Duration duration;
    private final Rectangle2D audioListRect = new Rectangle2D(100, 100, 250, 500);
    private final SimpleBooleanProperty audioListExpanded = new SimpleBooleanProperty();
    private HBox audioListPane;
    private Rectangle audioClipRect;
    private Timeline audioListRight;
    private Timeline audioListLeft;
    private final StackPane leftAudioArrow = StackPaneBuilder.create().style("-fx-padding: 8px 5px 0px 5px;-fx-background-color: black;-fx-shape: \"M0 .5 L1 1 L1 0 Z\";").maxHeight(10).maxWidth(10).build();
    private final StackPane rightAudioArrow = StackPaneBuilder.create().style("-fx-padding: 8px 5px 0px 5px;-fx-background-color: black;-fx-shape: \"M0 0 L1 .5 L0 1 Z\";").maxHeight(10).maxWidth(10).build();
    private final Label audioList = LabelBuilder.create().text("").graphic(leftAudioArrow).contentDisplay(ContentDisplay.CENTER).build();
    
    
    int videos=0;
    MediaView mediaView=new MediaView();
    Duration videoDuration;
    int video_playing=-1;
    int video_selected;
    private final Rectangle2D videoListRect = new Rectangle2D(100, 100, 250, 500);
    private final SimpleBooleanProperty videoListExpanded = new SimpleBooleanProperty();
    private HBox videoListPane;
    private Rectangle videoClipRect;
    private Timeline videoListRight;
    private Timeline videoListLeft;
    private final StackPane leftVideoArrow = StackPaneBuilder.create().style("-fx-padding: 8px 5px 0px 5px;-fx-background-color: black;-fx-shape: \"M0 .5 L1 1 L1 0 Z\";").maxHeight(10).maxWidth(10).build();
    private final StackPane rightVideoArrow = StackPaneBuilder.create().style("-fx-padding: 8px 5px 0px 5px;-fx-background-color: black;-fx-shape: \"M0 0 L1 .5 L0 1 Z\";").maxHeight(10).maxWidth(10).build();
    private final Label videoList = LabelBuilder.create().text("").graphic(leftAudioArrow).contentDisplay(ContentDisplay.CENTER).build();
      
    
    private final StackPane downArrow = StackPaneBuilder.create().style("-fx-padding: 8px 5px 0px 5px;-fx-background-color: black;-fx-shape: \"M0 0 L1 0 L.5 1 Z\";").maxHeight(10).maxWidth(10).build();
    
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws MalformedURLException {
        
        
        
// ****************Pane********************************//        
        
        primaryStage.setTitle("Sur Media Player");
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30));

        GridPane playerPane=new GridPane();
        playerPane.setVgap(5);
        playerPane.setHgap(5);
        playerPane.setPadding(new Insets(10));
                       
        final AnchorPane audioAnchor=new AnchorPane();
        final AnchorPane videoAnchor=new AnchorPane();
//**********************SCENES******************************//        
        
        final Scene scene = new Scene(grid, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(550);
        primaryStage.setMinHeight(450);
        scene.getStylesheets().add(MyProject.class.getResource("/CSS/Login.css").toExternalForm());
                
        final Scene audioPlayer=new Scene(audioAnchor,710,510);
//        primaryStage.setScene(audioPlayer);
        audioPlayer.getStylesheets().add(MyProject.class.getResource("/CSS/mainPlayer.css").toExternalForm());
       
   //     final Scene videoPlayer=new Scene(videoAnchor,700,500);
    //    videoPlayer.getStylesheets().add(MyProject.class.getResource("/CSS/mainPlayer.css").toExternalForm());
        
//***********************************************************//        
          
//*****************************LOGIN*****************************//        
        Text scenetitle = new Text("Sur");
        scenetitle.setId("welcome-text");
        grid.add(scenetitle, 0, 0, 1, 1);
        GridPane.setHalignment(scenetitle, HPos.CENTER);
        
        final Text scenetitle1 = new Text("Sign in");
        scenetitle1.setId("welcome-text1");
        grid.add(scenetitle1, 0, 4, 1, 1);
        GridPane.setHalignment(scenetitle1, HPos.CENTER);

        final TextField userTextField = new TextField();
        userTextField.setPrefSize(250, 30);
        userTextField.setId("textfield");
        userTextField.setPromptText("Username");
        grid.add(userTextField, 0, 6);

        final PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 0, 8);
        pwBox.setId("textfield");
        pwBox.setPrefSize(250, 30);
        pwBox.setPromptText("Password");

        final Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 11);

        final Text actiontarget = new Text("");
        grid.add(actiontarget, 0, 14);
        actiontarget.setId("notice");

        Button signup=new Button("Create an account");
        HBox hb=new HBox(10);
        signup.setAlignment(Pos.CENTER);
        hb.setAlignment(Pos.BOTTOM_CENTER);
        hb.getChildren().add(signup);
        grid.add(hb, 0, 20);
        signup.setId("signup");
        
        Button forgot=new Button("Forgot Password");
        HBox hbfor = new HBox(10);
        hbfor.setAlignment(Pos.BOTTOM_CENTER);
        hbfor.getChildren().add(forgot);
        grid.add(hbfor, 0, 18);
        forgot.setId("forgot");
                
//*******************************************************************//        
       
//********************************CREATE ACCOUNT****************************************//
        
        final GridPane grid1 = new GridPane();
        grid1.setAlignment(Pos.TOP_CENTER);
        grid1.setHgap(10);
        grid1.setVgap(10);
        grid1.setPadding(new Insets(30));
        
        Text scenetitles = new Text("Sur");
        scenetitles.setId("welcome-text");
        grid1.add(scenetitles, 0, 0, 1, 1);
        GridPane.setHalignment(scenetitles, HPos.CENTER);
        
        Text scenetitle2 = new Text("Create an Account");
        scenetitle2.setId("welcome-text1");
        grid1.add(scenetitle2, 0, 3, 1, 1);
        GridPane.setHalignment(scenetitle2, HPos.CENTER);
        
        final TextField userName = new TextField();
        userName.setPrefSize(250, 30);
        userName.setId("textfield");
        userName.setPromptText("Username");
        grid1.add(userName, 0, 5);

        final PasswordField password = new PasswordField();
        grid1.add(password, 0, 7);
        password.setId("textfield");
        password.setPrefSize(250, 30);
        password.setPromptText("Password");
        
        final ComboBox sec_ques=new ComboBox();
        sec_ques.getItems().addAll("What is your middle name?","What's the name of your High School teacher?","What is the name of your favorite T.V. show?");
        grid1.add(sec_ques, 0, 10);
        sec_ques.setPromptText("Security Question...");
        sec_ques.setId("textfield");
        
        final TextField answer = new TextField();
        answer.setPrefSize(250, 30);
        answer.setId("textfield");
        answer.setPromptText("Answer");
        grid1.add(answer, 0, 12);
        
        final Text action=new Text("");
        action.setId("notice");
        grid1.add(action, 0, 16);
        
        Button create=new Button("Sign Up");
        HBox hb2=new HBox(10);
        hb2.setAlignment(Pos.BOTTOM_CENTER);
        hb2.getChildren().add(create);
        grid1.add(hb2, 0, 15);
        
        final Button cancel1=new Button("Cancel");
        HBox hb1=new HBox(10);
        hb1.setAlignment(Pos.BOTTOM_CENTER);
        hb1.getChildren().add(cancel1);
        grid1.add(hb1, 0, 18);
        cancel1.setId("signup");
        
        signup.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                scene.setRoot(grid1);
            } 
        });
        
        cancel1.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                scene.setRoot(grid);
                action.setText("");
                cancel1.setText("Cancel");
                userTextField.setText("");
                pwBox.setText("");
                actiontarget.setText("");
            }
            
        });
        
        create.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                if(userName.getText().equals(""))
                            {
                                action.setText("Please enter Username.");
                                userName.setFocusTraversable(true);
                            }
                            else if(password.getText().equals(""))
                            {
                                action.setText("Please enter Password.");
                                password.setFocusTraversable(true);
                            }
                            else  if(sec_ques.getValue()==null)
                            {
                                action.setText("Please choose and answer the security question.");
                            }
                            else if(answer.getText().equals(""))
                            {
                                action.setText("Please answer security question.");
                                sec_ques.setFocusTraversable(true);
                            }
                            else
                            {
                                try
                                {
                                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                                    con=DriverManager.getConnection("jdbc:odbc:mydr");
                                    prepare=con.prepareStatement("insert into users values(?,?,?,?,?)");
                                    prepare.setString(1,userName.getText());
                                    prepare.setString(2, password.getText());
                                    prepare.setString(3, sec_ques.getValue().toString());
                                    prepare.setString(4, answer.getText());
                                    prepare.setString(5, "Purple Butterflies");
                                    prepare.executeUpdate();
                                    
                                    tableName=new String(userName.getText().toLowerCase());
                                    userName.setText("");
                                    password.setText("");
                                    sec_ques.setValue("Security Question...");
                                    answer.setText("");
                                    action.setText("Account successfully created. Please 'Go Back' and Sign In");
                                    cancel1.setText("Go Back");
                                }
                                catch(Exception ex)
                                {
                                    action.setText("Username already exists. Please try another username.");
                                    System.out.println(ex);                     
                                }
                                
                                try
                                {
                                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                                    con=DriverManager.getConnection("jdbc:odbc:mydr");
                                    prepare=con.prepareStatement("create table "+tableName+"Audio ( songID int primary key, name char(100) not null, path char(200) not null)");
                                    prepare.executeUpdate();
                                    PreparedStatement prepare1=con.prepareStatement("create table "+tableName+"Video ( videoID int primary key, name char(100) not null, path char(200) not null)");
                                    prepare1.executeUpdate();
                                }
                                catch(Exception ex)
                                {
                                    System.out.println(ex);
                                }
                            }
            }            
        });
        
        
//*****************************FORGOT PASSWORD*******************************************//
        
        final GridPane grid2 = new GridPane();
        grid2.setAlignment(Pos.TOP_CENTER);
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.setPadding(new Insets(30));
        
        Text scenetitle3 = new Text("Sur");
        scenetitle3.setId("welcome-text");
        grid2.add(scenetitle3, 0, 0, 1, 1);
        GridPane.setHalignment(scenetitle3, HPos.CENTER);
        
        final Text scenetitle4 = new Text("Reset Your Password");
        scenetitle4.setId("welcome-text1");
        grid2.add(scenetitle4, 0, 4, 1, 1);
        GridPane.setHalignment(scenetitle4, HPos.CENTER);

        final TextField forgotUser = new TextField();
        forgotUser.setPrefSize(250, 30);
        forgotUser.setId("textfield");
        forgotUser.setPromptText("Username");
        grid2.add(forgotUser, 0, 6);
        
        final Text action1=new Text("");
        action1.setId("notice");
        grid2.add(action1, 0, 10);        

        Button next1=new Button("Next >>");
        HBox hbNext1=new HBox(10);
        hbNext1.setAlignment(Pos.BOTTOM_CENTER);
        hbNext1.getChildren().add(next1);
        grid2.add(hbNext1, 0, 16);
        
        Button cancel=new Button("Cancel");
        HBox hbCan=new HBox(10);
        hbCan.getChildren().add(cancel);
        hbCan.setAlignment(Pos.BOTTOM_CENTER);
        grid2.add(hbCan, 0, 24);
        cancel.setId("signup");

        final GridPane grid3 = new GridPane();
        grid3.setAlignment(Pos.TOP_CENTER);
        grid3.setHgap(10);
        grid3.setVgap(10);
        grid3.setPadding(new Insets(30));
        
        Text scenetitle5 = new Text("Sur");
        scenetitle5.setId("welcome-text");
        grid3.add(scenetitle5, 0, 0, 1, 1);
        GridPane.setHalignment(scenetitle5, HPos.CENTER);
        
        final Text scenetitle6 = new Text("Answer Security Question");
        scenetitle6.setId("welcome-text1");
        grid3.add(scenetitle6, 0, 4, 1, 1);
        GridPane.setHalignment(scenetitle6, HPos.CENTER);
        
        final Label forgotQues=new Label();
        forgotQues.setId("textfield1");
        grid3.add(forgotQues, 0, 6);
        GridPane.setHalignment(forgotQues, HPos.CENTER);
        
        final TextField forgotAns=new TextField();
        forgotAns.setId("textfield");
        grid3.add(forgotAns, 0, 8);
        GridPane.setHalignment(forgotAns, HPos.CENTER);
        
        final Text action2=new Text("");
        action2.setId("notice");
        grid3.add(action2, 0, 12);
        
        Button next2=new Button("Next >>");
        HBox hbNext2=new HBox(10);
        hbNext2.setAlignment(Pos.BOTTOM_CENTER);
        hbNext2.getChildren().add(next2);
        grid3.add(hbNext2, 0, 16);
        
        Button cancel2=new Button("Cancel");
        HBox hbCan2=new HBox(10);
        hbCan2.getChildren().add(cancel2);
        hbCan2.setAlignment(Pos.BOTTOM_CENTER);
        grid3.add(hbCan2, 0, 20);
        cancel2.setId("signup");
        
        final GridPane grid4 = new GridPane();
        grid4.setAlignment(Pos.TOP_CENTER);
        grid4.setHgap(10);
        grid4.setVgap(10);
        grid4.setPadding(new Insets(30));
        
        Text scenetitle7 = new Text("Sur");
        scenetitle7.setId("welcome-text");
        grid4.add(scenetitle7, 0, 0, 1, 1);
        GridPane.setHalignment(scenetitle7, HPos.CENTER);
        
        final Text scenetitle8 = new Text("Reset Your Password");
        scenetitle8.setId("welcome-text1");
        grid4.add(scenetitle8, 0, 4, 1, 1);
        GridPane.setHalignment(scenetitle8, HPos.CENTER);

        final PasswordField nwPw1=new PasswordField();
        nwPw1.setId("textfield");
        grid4.add(nwPw1, 0, 6);
        GridPane.setHalignment(nwPw1, HPos.CENTER);
        nwPw1.setPromptText("New Password");
        
        final PasswordField nwPw2=new PasswordField();
        nwPw2.setId("textfield");
        grid4.add(nwPw2, 0, 8);
        GridPane.setHalignment(nwPw2, HPos.CENTER);
        nwPw2.setPromptText("Re-enter new Password");
        
        final Text action3=new Text("");
        action3.setId("notice");
        grid4.add(action3, 0, 12);
        
        Button next3=new Button("Reset Password");
        HBox hbNext3=new HBox(10);
        hbNext3.setAlignment(Pos.BOTTOM_CENTER);
        hbNext3.getChildren().add(next3);
        grid4.add(hbNext3, 0, 18);
        
        final Button cancel3=new Button("Cancel");
        HBox hbCan3=new HBox(10);
        hbCan3.getChildren().add(cancel3);
        hbCan3.setAlignment(Pos.BOTTOM_CENTER);
        grid4.add(hbCan3, 0, 21);
        cancel3.setId("signup");

        
        forgot.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                scene.setRoot(grid2);
            }
            
        });
        
        next1.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                try
                {
                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                    con=DriverManager.getConnection("jdbc:odbc:mydr");
                    prepare=con.prepareStatement("select username, sec_ques, answer from users");
                    result=prepare.executeQuery();
                    while(result.next())
                    {    
                        if(result.getString(1).replaceAll(" ", "").toLowerCase().equals(forgotUser.getText().toLowerCase()))
                        {
                            forgotQues.setText(result.getString(2));
                            ans=new String(result.getString(3));
                            scene.setRoot(grid3);
                        }
                        else
                        {
                            action1.setText("Username doesnot exist.");
                        }
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }
        });
        
        next2.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                if(forgotAns.getText().equals(ans.replaceAll(" ", "")))
                {
                    scene.setRoot(grid4);
                }
                else
                {
                    action2.setText(" Answer to security question is incorrect.");
                    action1.setText("");
                }
            }
        });
        
        next3.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                if(nwPw1.getText().equals(nwPw2.getText()))
                {
                    try
                    {
                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                    con=DriverManager.getConnection("jdbc:odbc:mydr");
                    String user=forgotUser.getText();
                    prepare=con.prepareStatement("update users set password=? where username=?");
                    prepare.setString(1, nwPw1.getText());
                    prepare.setString(2, user);
                    prepare.executeUpdate();
                    action3.setText("Your password has been reset.");
                    cancel3.setText("Go Back");
                    action2.setText("");
                    }
                    catch(Exception e)
                    {
                        
                    }
                }
                else
                {
                    action3.setText("Passwords doesnot match.");
                }
            }
        });
        
        cancel.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                scene.setRoot(grid);
                action1.setText("");
                action2.setText("");
                action3.setText("");
                forgotUser.setText("");
                forgotQues.setText("");
                forgotAns.setText("");
                nwPw1.setText("");
                nwPw2.setText("");
            }
        });
        
        cancel2.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                scene.setRoot(grid);
            }
            
        });
        
        cancel3.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                scene.setRoot(grid);
                cancel3.setText("Cancel");
            }
            
        });
        
//****************************************************************************************//
        
        
//**********************AUDIO/VIDEO Option***************************//
        final ToggleGroup audio_video=new ToggleGroup();
        final ToggleButton audio=new ToggleButton("Audio");
        audio.setToggleGroup(audio_video);
        audio.setUserData(audioAnchor);
        
        final ToggleButton video=new ToggleButton("Video");
        video.setToggleGroup(audio_video);
        video.setUserData(videoAnchor);
        
        AnchorPane.setTopAnchor(audio, 10.0);
        AnchorPane.setLeftAnchor(audio, 60.0);
        
        AnchorPane.setTopAnchor(video, 10.0);
        AnchorPane.setLeftAnchor(video, 140.0);
        
        audioAnchor.getChildren().addAll(audio,video);
        
        btn.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) 
            {
                int hasResult=0;
                
                if(userTextField.getText().isEmpty())
                {
                    actiontarget.setText("Please enter username");
                }
                else if(pwBox.getText().isEmpty())
                {
                    actiontarget.setText("Please enter password");
                }
                else
                {
                try
                {
                    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                    con=DriverManager.getConnection("jdbc:odbc:mydr");
                    prepare=con.prepareStatement("select * from users");
                    result=prepare.executeQuery();
                    while(result.next())
                    {
                        //hasResult=1;
                        String user=result.getString(1);
                        String pass=result.getString(2);
                        user=user.replaceAll(" ", "");
                        pass=pass.replaceAll(" ", "");
                        if(user.equals(userTextField.getText()))
                        {
                            hasResult=2;
                            if(pass.equals(pwBox.getText().toString()))
                            {
                                hasResult=3;
                                username=userTextField.getText().toLowerCase();
                                char[] digit=username.toCharArray();
                                digit[0]=Character.toUpperCase(digit[0]);
                                System.out.println(digit);
                                username=new String(digit);
                                System.out.println(username);
                                break;
                            }
                            
                            else
                            {
                                actiontarget.setText("Pasword is incorrect.");
                            }
                        }
                        
                        else if(!user.equals(userTextField.getText()))
                        {
                            actiontarget.setText("Username does not exist.");
                        }
                    }
                }
                catch(Exception ex)
                {
                    
                }
                }
                if(hasResult==0)
                {
                    //actiontarget.setText("Username dosnot exist.");
                }
                else if(hasResult==1)
                {
                    if(userTextField.getText().isEmpty())
                    {
                            actiontarget.setText("Please enter a username.");
                    }
                    else
                    {
                        actiontarget.setText("1Username doesnot exist.");
                    }
                }
                else if(hasResult==2)
                {
                    if(pwBox.getText().isEmpty())
                    {
                            actiontarget.setText("Please enter your password.");
                    }
                    else
                    {
                        actiontarget.setText("Entered password is incorrect.");
                    }
                }
                else
                {
                primaryStage.setScene(audioPlayer);
                primaryStage.setResizable(true);
                audio.setSelected(true);
                
        audio_video.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle ol_val, Toggle new_val)
            {
                if(new_val==null)
                {

                }
                
                else if(ol_val==video && new_val==audio)
                {
                    audioPlayer.setRoot((AnchorPane) audio_video.getSelectedToggle().getUserData());
                    audioAnchor.getChildren().addAll(audio,video);
                }
                else if(ol_val==audio && new_val==video)
                {
                    audioPlayer.setRoot((AnchorPane) audio_video.getSelectedToggle().getUserData());
                    videoAnchor.getChildren().addAll(audio,video);
                }
            }
        });
       
        
//************************************************************************************************************//       
        
//******************************CODING FOR AUDIO***************************************************************//
        
//*************************************************************************************************************//        
       /* MenuBar menuBar=new MenuBar();
        audioAnchor.getChildren().add(menuBar);
        AnchorPane.setTopAnchor(menuBar, 5.0);
        AnchorPane.setLeftAnchor(menuBar, 5.0);
        
        Menu menu=new Menu();
        menu.setGraphic(downArrow);
        menuBar.getMenus().add(menu);
        menuBar.setId("menubar");
        menu.setId("menubar");
        
        */
        
//*****************************MAIN PLAYER OPEN*************************//        
        
        
        final Slider progressbar=new Slider();
        progressbar.setMax(100);
        progressbar.setMinWidth(690);
        progressbar.setMaxWidth(690);
        progressbar.setBlockIncrement(10);
        
        final ProgressBar progBar=new ProgressBar(0);
        progBar.setMinWidth(690);
        progBar.setMaxWidth(690);
        progBar.prefHeight(5);
        progBar.getStyleClass().add("progress");
        
        final StackPane showProgress=new StackPane();
        showProgress.getChildren().addAll(progBar,progressbar);
//        playerPane.add(showProgress, 0, 40,10,1);
        AnchorPane.setBottomAnchor(showProgress, 80.0);
//        AnchorPane.setRightAnchor(showProgress, 20.0);
        AnchorPane.setLeftAnchor(showProgress, audioPlayer.getWidth()/2-345);
        
        
        final HBox mediaButtons=new HBox(10);
        mediaButtons.setAlignment(Pos.CENTER);
                
        final Button play=new Button("\u25B6");
        play.setId("mediaPlay");
              
        final Button pause=new Button("||");
        pause.setId("mediaPlay");
        
        final Button next=new Button("\u23e9");
        next.setId("mediaNext");
        
        final Button previous=new Button("\u23ea");
        previous.setId("mediaNext");
          
        mediaButtons.getChildren().addAll(previous,play,next);
        
        
 //       GridPane.setHalignment(playerPane, HPos.CENTER);
  //      playerPane.add(mediaButtons,4,41,2,1);
        AnchorPane.setBottomAnchor(mediaButtons, 20.0);
        AnchorPane.setLeftAnchor(mediaButtons, (audioPlayer.getWidth()/2)-90);
                
        final Slider volSlider=new Slider();
        volSlider.setMax(100);
        volSlider.setMinWidth(100);
        volSlider.setMaxWidth(100);
        volSlider.setValue(20);
        
        final ProgressBar volProgress=new ProgressBar(0);
        volProgress.setMinWidth(100);
        volProgress.setMaxWidth(100);
        volProgress.prefHeight(5);
        volProgress.getStyleClass().add("progress");
        volProgress.setProgress(0.2);
        
        final StackPane volume=new StackPane();
        volume.getChildren().addAll(volProgress,volSlider);
        HBox.setHgrow(volProgress, Priority.ALWAYS);
        HBox.setHgrow(volSlider, Priority.ALWAYS);
        
        audioAnchor.getChildren().addAll(showProgress,mediaButtons,volume);
        AnchorPane.setBottomAnchor(volume, 40.0);
        AnchorPane.setRightAnchor(volume, audioPlayer.getWidth()/2-350);
          
        //*****************************ADDING AUDIO METADATA*****************************//
        final Label title = new Label();
        title.setId("metadata");
        final Label artist=new Label();
        artist.setId("metadata");
        final Label album=new Label();
        album.setId("metadata");
        final Label year=new Label();
        year.setId("metadata");
        final Label genre=new Label();
        genre.setId("metadata");
        final Label length=new Label();
        length.setId("metadata");
//        length.setText(mediaPlayer.getTotalDuration().toString());
        
        //null,album,artist,year;
        final ImageView albumCover=new ImageView();
        albumCover.setFitHeight(200);
        albumCover.setFitWidth(200);
        final Reflection reflection=new Reflection();
        
        reflection.setFraction((audioPlayer.getHeight()-200)*0.001);
        
        albumCover.setEffect(reflection);
        
        AnchorPane.setLeftAnchor(title, 30.0);
        AnchorPane.setTopAnchor(title, 60.0);
     //   audioAnchor.getChildren().add(title);
        
        AnchorPane.setLeftAnchor(artist, audioPlayer.getWidth()/2+130);
        AnchorPane.setTopAnchor(artist, audioPlayer.getHeight()/2-120);
       // audioAnchor.getChildren().add(artist);
        
        AnchorPane.setLeftAnchor(album, audioPlayer.getWidth()/2+130);
        AnchorPane.setTopAnchor(album, audioPlayer.getHeight()/2-100);
     //   audioAnchor.getChildren().add(album);
        
        AnchorPane.setLeftAnchor(year, audioPlayer.getWidth()/2+130);
        AnchorPane.setTopAnchor(year, audioPlayer.getHeight()/2-80);
   //     audioAnchor.getChildren().add(year);
        
        AnchorPane.setLeftAnchor(genre, audioPlayer.getWidth()/2+130);
        AnchorPane.setTopAnchor(genre, audioPlayer.getHeight()/2-60);
        
        AnchorPane.setLeftAnchor(length, audioPlayer.getWidth()/2+130);
        AnchorPane.setTopAnchor(length, audioPlayer.getHeight()/2-40);
        
        AnchorPane.setLeftAnchor(albumCover, audioPlayer.getWidth()/2-100);
        AnchorPane.setTopAnchor(albumCover, (audioPlayer.getHeight()/2)-120);
        audioAnchor.getChildren().addAll(title,artist,year,albumCover,album,length,genre);

        volSlider.valueProperty().addListener(new ChangeListener<Number>()
                {
                    @Override
                    public void changed(ObservableValue<?extends Number> ob, Number old_val, Number new_val)
                    {
                        volProgress.setProgress(new_val.doubleValue()/100);
                    }
                });

        
        
//******************************Audio List****************************************//

                audioListPane=new HBox();
		audioListPane.setPrefSize(audioListRect.getWidth(), audioListRect.getHeight());
		
		final StackPane sp1 = new StackPane();
		
		sp1.setAlignment(Pos.TOP_LEFT);
		sp1.setStyle("-fx-background-color:#ead2f7;-fx-background-insets:0,1.5;-fx-opacity:.92;-fx-background-radius:0px 0px 0px 5px;");
		sp1.setPrefSize(audioListRect.getWidth(), audioListRect.getHeight()-30.0);
		
		
		StackPane sp2 = new StackPane();
		sp2.setPrefSize(30.0, 30.0);
		sp2.getChildren().add(audioList);
		sp2.setStyle("-fx-cursor:hand;-fx-background-color:#CC99CC;-fx-border-width:1px 0px 1px 1px;-fx-border-color:#333333;-fx-opacity:.92;-fx-border-radius:5px 0px 0px 5px;-fx-background-radius:5px 0px 0px 5px;");
		sp2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent paramT) {
				if(audioListExpanded.get()){
			audioListExpanded.set(false);
		}else{
			audioListExpanded.set(true);
		}
			}
		});
                audioListPane.getChildren().addAll(sp1,sp2);
//		audioListPane.getChildren().addAll(GroupBuilder.create().children(sp1).build(),GroupBuilder.create().children(sp2).build());
		AnchorPane.setRightAnchor(audioListPane, -(audioListRect.getWidth()-18));
                AnchorPane.setTopAnchor(audioListPane, 0.0);
                audioAnchor.getChildren().add(audioListPane);
                
                
		/* Initial position setting for Top Pane*/
		audioClipRect = new Rectangle();
                audioClipRect.setWidth(30.0);
		audioClipRect.setHeight(audioListRect.getHeight());
		audioClipRect.translateXProperty().set(audioListRect.getWidth()-15.0);
		audioListPane.setClip(audioClipRect);
		audioListPane.translateXProperty().set(-(audioListRect.getWidth()-15.0));
			
		/* Animation for bouncing effect. */
		final Timeline timelineDown1 = new Timeline();
		timelineDown1.setCycleCount(2);
		timelineDown1.setAutoReverse(true);
		final KeyValue kv1 = new KeyValue(audioClipRect.widthProperty(), (audioListRect.getWidth()-15));
		final KeyValue kv2 = new KeyValue(audioClipRect.translateXProperty(), 15);
		final KeyValue kv3 = new KeyValue(audioListPane.translateXProperty(), -(audioListRect.getWidth()-30.0));
		final KeyFrame kf1 = new KeyFrame(Duration.millis(200), kv1, kv2, kv3);
		timelineDown1.getKeyFrames().add(kf1);
		
		/* Event handler to call bouncing effect after the scroll down is finished. */
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	timelineDown1.play();
            }
        };
        
        audioListLeft = new Timeline();
        audioListRight = new Timeline();
        
        /* Animation for scroll down. */
		audioListLeft.setCycleCount(1);
		audioListLeft.setAutoReverse(true);
		final KeyValue kvDwn1 = new KeyValue(audioClipRect.widthProperty(), audioListRect.getWidth());
		final KeyValue kvDwn2 = new KeyValue(audioClipRect.translateXProperty(), 0);
		final KeyValue kvDwn3 = new KeyValue(audioListPane.translateXProperty(), -(audioListRect.getWidth()-15.0));
		final KeyFrame kfDwn = new KeyFrame(Duration.millis(400), onFinished, kvDwn1, kvDwn2, kvDwn3);
		audioListLeft.getKeyFrames().add(kfDwn);
		
		/* Animation for scroll up. */
		audioListRight.setCycleCount(1); 
		audioListRight.setAutoReverse(true);
		final KeyValue kvUp1 = new KeyValue(audioClipRect.widthProperty(), 15.0);
		final KeyValue kvUp2 = new KeyValue(audioClipRect.translateXProperty(), audioListRect.getWidth()-15.0);
		final KeyValue kvUp3 = new KeyValue(audioListPane.translateXProperty(), -(audioListRect.getWidth()-15.0));
		final KeyFrame kfUp = new KeyFrame(Duration.millis(400), kvUp1, kvUp2, kvUp3);
		audioListRight.getKeyFrames().add(kfUp);
                
                
                audioListExpanded.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> paramObservableValue,Boolean paramT1, Boolean paramT2) {
				if(paramT2){
					// To expand
                                        audioListLeft.play();
					audioList.setGraphic(rightAudioArrow);
                                        
                                        AnchorPane.setLeftAnchor(volume, ((audioPlayer.getWidth()-250)/2)-150);
                                        AnchorPane.setLeftAnchor(mediaButtons, ((audioPlayer.getWidth()-250)/2)-90);
                                        progBar.setMinWidth(audioPlayer.getWidth()-290);
                                        progressbar.setMinWidth(audioPlayer.getWidth()-290);
                                        progBar.setMaxWidth(audioPlayer.getWidth()-290);
                                        progressbar.setMaxWidth(audioPlayer.getWidth()-290);
                                        AnchorPane.setLeftAnchor(albumCover, (audioPlayer.getWidth()-250)/2-200);
                                        AnchorPane.setLeftAnchor(artist, (audioPlayer.getWidth()-250)/2+20);
                                        AnchorPane.setLeftAnchor(album, (audioPlayer.getWidth()-250)/2+20);
                                        AnchorPane.setLeftAnchor(year, (audioPlayer.getWidth()-250)/2+20);
                                        AnchorPane.setLeftAnchor(genre, (audioPlayer.getWidth()-250)/2+20);
                                        AnchorPane.setLeftAnchor(length, (audioPlayer.getWidth()-250)/2+20);
                                        
				}else{
					// To close
                                        audioListRight.play();
					audioList.setGraphic(leftAudioArrow);
                                        
                                        AnchorPane.setLeftAnchor(volume, audioPlayer.getWidth()/2+100);
                    AnchorPane.setLeftAnchor(mediaButtons, (audioPlayer.getWidth()/2)-90);
                    progBar.setMinWidth(audioPlayer.getWidth()-40);
                    progressbar.setMinWidth(audioPlayer.getWidth()-40);
                    progBar.setMaxWidth(audioPlayer.getWidth()-40);
                    progressbar.setMaxWidth(audioPlayer.getWidth()-40);
                    AnchorPane.setLeftAnchor(showProgress, 20.0);
                    AnchorPane.setLeftAnchor(albumCover, audioPlayer.getWidth()/2-100);
                    AnchorPane.setLeftAnchor(artist, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(album, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(year, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(genre, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(length, audioPlayer.getWidth()/2+130);
				}
			}
		});
                
                
//*****************************************************************************//
                

        //******************LIST OF AUDIO*******************************//
        
        final VBox audioListBox=new VBox();
        final ScrollPane audioScroll=new ScrollPane(); 
        
        AnchorPane audioListAnchor=new AnchorPane();
        final Button songList[]=new Button[100];
//        File songFile[]=new File[100];
        final File songFile[] = new File[100];
        int[] songID=new int[100];
        
        final Media medias[]=new Media[100];
        final MediaPlayer mediaPlayers[]=new MediaPlayer[100];
        int j=0;
        for(int i=0;i<100;i++)
        {
            songList[i]=new Button();
            songList[i].setId("audio_list");
            songList[i].setMinWidth(230.0);
            songList[i].setMaxWidth(230.0);
            //AnchorPane.setTopAnchor(songList[i], 200.0+20*i);
        }
        
        try
        {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        con=DriverManager.getConnection("jdbc:odbc:mydr");
        prepare=con.prepareStatement("select * from "+username.toLowerCase()+"Audio");
        result=prepare.executeQuery();
        
        String song_name;
        while(result.next())
        {
            songID[j]=Integer.parseInt(result.getString(1));
            song_name=result.getString(2);
            songList[j].setText(song_name);
//            songFile[j]=new File(result.getString(3));
            songFile[j]=new File(result.getString(3));
            medias[j]=new Media(songFile[j].toURI().toURL().toString());
            mediaPlayers[j]=new MediaPlayer(medias[j]);
            audioListBox.getChildren().add(songList[j]);
            //audioListAnchor.getChildren().add(songList[j]);
            j++;
            songs++;
            audio_playing=0;
        }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        
        audioScroll.setContent(audioListBox);
        audioScroll.setPrefSize(audioListRect.getWidth()-15.0, audioListRect.getHeight()-150.0);
        final Button addAudio=new Button("Add Song...");
        AnchorPane.setBottomAnchor(addAudio, 10.0);
        AnchorPane.setTopAnchor(audioScroll, 50.0);
        
        Text wel1=new Text("Welcome, ");
        AnchorPane.setTopAnchor(wel1,10.0);
        AnchorPane.setLeftAnchor(wel1, 10.0);
        
        final Button wel2=new Button(username);
        AnchorPane.setTopAnchor(wel2,8.0);
        AnchorPane.setLeftAnchor(wel2, 55.0);
        wel2.setId("user");
        
        final ContextMenu userInfo=new ContextMenu();
        MenuItem prof=new MenuItem("Edit Password");
        
        MenuItem logout=new MenuItem("Logout");
        
        userInfo.getItems().addAll(prof, logout);
        
        audioListAnchor.getChildren().addAll(audioScroll, addAudio,wel1,wel2);
        
        wel2.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent e)
                {
                    if(e.getButton().equals(MouseButton.SECONDARY))
                    {
                        wel2.setContextMenu(userInfo);
                    }
                }
            });
            
        logout.setOnAction(new EventHandler<ActionEvent>()
        {
                    public void handle(ActionEvent t) 
                    {
                        username="";
                        userTextField.setText("");
                        pwBox.setText("");
                        primaryStage.setScene(scene);
                        actiontarget.setText("");
                    }
            
        }
        );
        
        sp1.getChildren().addAll(audioListAnchor);
        
        
        //**********************
        final GridPane profGrid=new GridPane();
        profGrid.setAlignment(Pos.TOP_CENTER);
        profGrid.setHgap(10);
        profGrid.setVgap(10);
        profGrid.setPadding(new Insets(30));
        //final Scene profile=new Scene(profGrid,500,400);
        //profile.getStylesheets().add(MyProject.class.getResource("/CSS/mainPlayer.css").toExternalForm());
    
        Text profL=new Text("Edit Password");
        profL.setId("profile");
        GridPane.setHalignment(profL, HPos.CENTER);
        profGrid.add(profL, 0, 0);
        
        final PasswordField newPw1=new PasswordField();
        GridPane.setHalignment(newPw1, HPos.CENTER);
        profGrid.add(newPw1, 0, 4);
        newPw1.setId("textfield1");
        newPw1.setPromptText("Old Password");
        newPw1.setPrefSize(250, 30);
        
        final PasswordField newPw2=new PasswordField();
        GridPane.setHalignment(newPw2, HPos.CENTER);
        profGrid.add(newPw2, 0, 6);
        newPw2.setPrefSize(250, 30);
        newPw2.setPromptText("New Password");
        newPw2.setId("textfield1");
        
        final PasswordField newPw3=new PasswordField();
        GridPane.setHalignment(newPw3, HPos.CENTER);
        profGrid.add(newPw3, 0, 8);
        newPw3.setPrefSize(250, 30);
        newPw3.setPromptText("Re-type New Password");
        newPw3.setId("textfield1");
        
        final Text actionNew=new Text("");
        profGrid.add(actionNew, 0, 11);
        actionNew.setId("notice");
        
        Button changeP=new Button("Change Password");
        profGrid.add(changeP, 0, 13);
        GridPane.setHalignment(changeP, HPos.CENTER);
        changeP.setId("profB");
        
        final Button goBack=new Button("Cancel");
        profGrid.add(goBack, 0, 15);
        GridPane.setHalignment(goBack, HPos.CENTER);
        goBack.setId("profBt");
        
        prof.setOnAction(new EventHandler<ActionEvent>()
        {
                    public void handle(ActionEvent t) 
                    {
                        //primaryStage.setScene(profile);
                        audioPlayer.setRoot(profGrid);
                        goBack.setText("Cancel");
                    }
        });
        
        goBack.setOnAction(new EventHandler<ActionEvent>()
        {
                    public void handle(ActionEvent t) 
                    {
                        audioPlayer.setRoot((AnchorPane)audio_video.getSelectedToggle().getUserData());
                    }
        });
        
        changeP.setOnAction(new EventHandler<ActionEvent>()
        {
                    public void handle(ActionEvent t) 
                    {
                        if(newPw1.getText().isEmpty())
                        {
                            actionNew.setText("Enter your existing password.");
                        }
                        else if(newPw2.getText().isEmpty())
                        {
                            actionNew.setText("Enter new password.");
                        }
                        else if(newPw3.getText().isEmpty())
                        {
                            actionNew.setText("Re-enter new password.");
                        }
                        else if(!newPw2.getText().equals(newPw3.getText()))
                        {
                            actionNew.setText("New Password and re-entered new password does not match.");
                        }
                        else
                        {
                            int y = 0;
                        try
                        {
                            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                            con=DriverManager.getConnection("jdbc:odbc:mydr");
                            prepare=con.prepareStatement("select username, password from users");
                            result=prepare.executeQuery();
                            while(result.next())
                            {
                                if(result.getString(1).replaceAll(" ", "").equals(username.toLowerCase()))
                                {
                                    if(result.getString(2).replaceAll(" ", "").equals(newPw1.getText()))
                                    {
                                        System.out.println("hi");
                                        y=1;
                                    }
                                }
                            }
                        }
                        catch(Exception ex)
                        {
                            
                        }
                        if(y!=1)
                        {
                            actionNew.setText("Old Password entered is incorrect.");
                        }
                        else
                        {
                        try
                        {
                            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                            con=DriverManager.getConnection("jdbc:odbc:mydr");
                            prepare=con.prepareStatement("update users set password=? where username=?");
                            prepare.setString(1, newPw2.getText());
                            prepare.setString(2, username.toLowerCase());
                            prepare.executeUpdate();
                            actionNew.setText("Your password has been changed.");
                            goBack.setText("Go Back");
                        }
                        catch(Exception ex)
                        {
                            System.out.println(ex);
                        }
                        }
                        }
                    }
        });
        
        
        
        //**************************
        //****************************METADATA**************************************************//
                 
               final String Title[] = new String[100];
               final String Artist[]=new String[100];
               final String Album[]=new String[100];
               final String Year[]=new String[100];
               final String Genre[]=new String[100];
               final String Length[]=new String[100];
               
//        length.setText(mediaPlayer.getTotalDuration().toString());
        
        
        //null,album,artist,year;
        final Image AlbumCover[]=new Image[100];
               //Label 
               for(int i=0;i<100;i++)
               {
                   Artist[i]=new String();
                   Title[i]=new String();
                   Album[i]=new String();
                   Year[i]=new String();
                   Genre[i]=new String();
                   Length[i]=new String();
                   AlbumCover[i]=new Image("/Images/song.png");
               }
        
               
      final Duration durations[]=new Duration[100];
        for(int i=0;i<songs;i++)
        {
           final int k=i;
           
           mediaPlayers[k].setOnEndOfMedia(new Runnable()
                   {
                     public void run()  
                     {
                         mediaPlayers[k].stop();
                         audio_playing++;
                         if(audio_playing>=songs)
                         {
                             
                         }
                         else
                         {
                             mediaPlayers[audio_playing].play();
                             mediaPlayers[audio_playing].currentTimeProperty().addListener(new InvalidationListener()
                             {
                                 @Override
                                 public void invalidated(Observable ob)
                                 {
                                     Duration currTime=mediaPlayers[audio_playing].getCurrentTime();
                                     progressbar.setValue(100.0*currTime.divide(durations[audio_playing]).toMillis());
                                 }
                                 
                             });
                             for(int k=0;k<100;k++)
                             {
                                 songList[k].setId("audio_list");
                             }
                             songList[audio_playing].setId("song_clicked");
                             mediaPlayers[audio_playing].setVolume(volSlider.getValue()/100.0);
                             album.setText(Album[audio_playing]);
                             title.setText(Title[audio_playing]);
                             artist.setText(Artist[audio_playing]);
                             
                             year.setText(Year[audio_playing]);
                             genre.setText(Genre[audio_playing]);
                             albumCover.setImage(AlbumCover[audio_playing]);
                             length.setText(Length[audio_playing]);
                             
                             //********//
                             durations[audio_playing]=mediaPlayers[audio_playing].getMedia().getDuration();
                             int intDuration=(int)Math.floor(durations[audio_playing].toSeconds());
                             int durationHours=intDuration/3600;
                             if (durationHours > 0) 
                             {
                                 intDuration -= durationHours * 60 * 60;
                             }
                             int durationMinutes = intDuration / 60;
                             int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                             if(durationHours>0)
                             {
                                 Length[audio_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                             }
                             else 
                             {
                                 Length[audio_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                                 System.out.println(durations[audio_playing]);
                             }
                             length.setText(Length[audio_playing]);
                         }
                     }
                   });
           //durations[k]=mediaPlayers[i].getMedia().getDuration();
           mediaPlayers[k].setOnPlaying(new Runnable()
//            mediaPlayers[k].setOnReady(new Runnable()
           {
               @Override
               public void run() 
               {
                   durations[k]=mediaPlayers[k].getMedia().getDuration();
                   int intDuration=(int)Math.floor(durations[k].toSeconds());
                   int durationHours=intDuration/3600;
                   if (durationHours > 0) 
                   {
                    intDuration -= durationHours * 60 * 60;
                   }
                   int durationMinutes = intDuration / 60;
                   int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                   if(durationHours>0)
                   {
                       Length[k]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                   }
                   else 
                   {
                       Length[k]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                       System.out.println(durations[k]);
                   }
           }
            });
        }
                 
                   
               for(int i=0;i<songs;i++)
               {
                   final int a=i;
                   medias[i].getMetadata().addListener(new MapChangeListener<String, Object>()
                   {
                       String key;
                       Object value;
                       @Override
                       public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> ch)
                       {
                           if(ch.wasAdded())
                           {
                               key=ch.getKey();
                               value=ch.getValueAdded();
                               if(key.equals("album"))
                               {
                                   Album[a]=value.toString();
                               }
                               if(key.equals("title"))
                               {
                                   Title[a]=value.toString();
                               }
                               if(key.equals("artist"))
                               {
                                   Artist[a]=value.toString();
                               }
                               if(key.equals("year"))
                               {
                                   Year[a]=value.toString();
                               }
                               if(key.equals("image"))
                               {
                                   AlbumCover[a]=(Image)value;
                               }
                               if(key.equals("genre"))
                               {
                                   Genre[a]=value.toString();
                               }
                               
                           }
                       }
                   });
                   
                   
               }
               
               
//****************************************************************************************//

        //*******************************Song Chooser**********************************//
        
        final FileChooser songSelect=new FileChooser();
        songSelect.setTitle("Add Songs");
        songSelect.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3 Songs", "*.mp3"));
        
        addAudio.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                int s=songs;
                if(audio_playing>=0)
                {
                mediaPlayers[audio_playing].pause();
                }
/*                for(int i=0;i<countSongs();i++)
                {
                    if(mediaPlayers[i].getStatus()==Status.PLAYING)
                    {
                        mediaPlayers[i].stop();
                        break;
                    }
                }*/
                List<File> newSongs=songSelect.showOpenMultipleDialog(primaryStage);
                if(newSongs!=null)
                {
                    int b=0;
                    for(File songfile:newSongs)
                    {
                        
                        try
                        {
                            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                            con=DriverManager.getConnection("jdbc:odbc:mydr");
                            prepare=con.prepareStatement("insert into "+username.toLowerCase()+"Audio values(?,?,?)");
                            prepare.setInt(1,s+b+1);
                            prepare.setString(2, songfile.getName());
                            prepare.setString(3, songfile.getPath());
                            prepare.executeUpdate();
                            b++;
                            songs++;
                         }
                        catch(Exception ex)
                        {
                            System.out.println(ex);
                        }   
                        
                        try
                        {
                            int id;
                            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                            con=DriverManager.getConnection("jdbc:odbc:mydr");
                            prepare=con.prepareStatement("select * from "+username.toLowerCase()+"Audio");
                            result=prepare.executeQuery();
                            while(result.next())
                            {
                                id=result.getInt(1)-1;
                                songList[id].setText(result.getString(2));
                                songFile[id]=new File(result.getString(3));
                                medias[id]=new Media(songFile[id].toURI().toURL().toString());
                                mediaPlayers[id]=new MediaPlayer(medias[id]);
                                audioListBox.getChildren().remove(songList[id]);
                                audioListBox.getChildren().add(songList[id]);
                            }
                        }
                        catch(Exception ex)
                        {
                            System.out.println(ex);
                        }
                        //openSongs(songfile,s);
                        for(int i=0;i<songs;i++)
        {
        songList[i].setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            int n=audio_selected;
            public void handle(final KeyEvent t) 
            {
             
            if( t.getCode().equals(KeyCode.DELETE))
            {
                System.out.println("Pressed");
                try
                       {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("delete from "+username.toLowerCase()+"Audio where songID="+(audio_selected+1));
                           prepare.executeUpdate();
                           n--;
                           songs--;
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                       }
                       try
                       {
                           for(int i=audio_selected;i<=songs;i++)
                           {
                               String[] l;
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("update "+username.toLowerCase()+"Audio set songID="+i+"where songID="+(i+1));
                           prepare.executeUpdate();
                           
                           songList[i].setText(songList[i+1].getText());
                           songFile[i]=new File(songFile[i+1].getPath());
                           medias[i]=new Media(songFile[i].toURI().toURL().toString());
                           mediaPlayers[i]=new MediaPlayer(medias[i]);
                           }
                           //audio_selected--;
                           songList[audio_selected].setId("song_clicked");
                       }
                       catch(Exception e)
                       {
                           System.out.println();
                       }
                       
                       audioListBox.getChildren().remove(songList[songs]);
            } 
            }
        });
        } 
        
                    }
                }
                if(audio_playing>0)
                {
                mediaPlayers[audio_playing].play();
                mediaPlayers[audio_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       Duration currTime=mediaPlayers[audio_playing].getCurrentTime();
                       progressbar.setValue(100.0*currTime.divide(durations[audio_playing]).toMillis());
                    }
                   
               });
                
                }
                else
                {
                audio_playing=0;
                }        
            }
        });
        
        
        //****************************************************************************//
                       

//***********************************Delete Audio*********************************************//
               
               final ContextMenu audio_delete=new ContextMenu();
               
               MenuItem audioDelete=new MenuItem("Delete");
               audio_delete.getItems().add(audioDelete);
               audioDelete.setOnAction(new EventHandler()
               {
                   int n=audio_selected;
                   public void handle(Event t) 
                   {
                       try
                       {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("delete from "+username.toLowerCase()+"Audio where songID="+(audio_selected+1));
                           prepare.executeUpdate();
                           n--;
                           songs--;
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                       }
                       try
                       {
                           for(int i=audio_selected;i<=songs;i++)
                           {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("update "+username.toLowerCase()+"Audio set songID="+i+"where songID="+(i+1));
                           prepare.executeUpdate();
                           
                           songList[i].setText(songList[i+1].getText());
                           songFile[i]=new File(songFile[i+1].getPath());
                           medias[i]=new Media(songFile[i].toURI().toURL().toString());
                           mediaPlayers[i]=new MediaPlayer(medias[i]);
                           }
                           //audio_selected--;
                           songList[audio_selected].setId("song_clicked");
                       }
                       catch(Exception e)
                       {
                           System.out.println();
                       }
                       
                       audioListBox.getChildren().remove(songList[songs]);
                   }
                   
               });
               
               
//********************************************************************************************//

//***************************************************************************/
        
        for(int i=0;i<songs;i++)
        {
        songList[i].setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            int n=audio_selected;
            public void handle(final KeyEvent t) 
            {
             
            if( t.getCode().equals(KeyCode.DELETE))
            {
                System.out.println("Pressed");
                try
                       {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("delete from "+username.toLowerCase()+"Audio where songID="+(audio_selected+1));
                           prepare.executeUpdate();
                           n--;
                           songs--;
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                       }
                       try
                       {
                           for(int i=audio_selected;i<=songs;i++)
                           {
                               String[] l;
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("update "+username.toLowerCase()+"Audio set songID="+i+"where songID="+(i+1));
                           prepare.executeUpdate();
                           
                           songList[i].setText(songList[i+1].getText());
                           songFile[i]=new File(songFile[i+1].getPath());
                           medias[i]=new Media(songFile[i].toURI().toURL().toString());
                           mediaPlayers[i]=new MediaPlayer(medias[i]);
                           }
                           //audio_selected--;
                           songList[audio_selected].setId("song_clicked");
                       }
                       catch(Exception e)
                       {
                           System.out.println();
                       }
                       
                       audioListBox.getChildren().remove(songList[songs]);
            } 
            }
        });
        } 
        
        for(int i=0;i<100;i++)
        {
            final int m=i;
            songList[i].setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent e)
                { 
                    if(e.getButton().equals(MouseButton.SECONDARY))
                    {
                        songList[m].setContextMenu(audio_delete);
                    }
                    if(e.getButton().equals(MouseButton.PRIMARY))
                    {
                        if(e.getClickCount()==1)
                        {
                            for(int k=0;k<100;k++)
                            {
                                songList[k].setId("audio_list");
                            }
                            songList[m].setId("song_clicked");
                            audio_selected=m;
                        }
                        if(e.getClickCount()==2)
                        {

                            for(int k=0;k<songs;k++)
                            {
                                    if(k==m)
                                    {
                                        mediaPlayers[m].play();
                                        audio_playing=m;
                                        mediaPlayers[audio_playing].currentTimeProperty().addListener(new InvalidationListener()
                                        {
                                            @Override
                                            public void invalidated(Observable ob)
                                            {
                                                try
                                                {
                                                Duration currTime=mediaPlayers[audio_playing].getCurrentTime();
                                                progressbar.setValue(100.0*currTime.divide(durations[audio_playing]).toMillis());
                                                }
                                                catch(Exception ex)
                                                {
                                                    System.out.println(ex);
                                                    audio_playing--;
                                                    mediaButtons.getChildren().clear();
                                                    mediaButtons.getChildren().addAll(previous,play,next);  
                                                }
                                            }
                                            
                                        });
                                        
                                        
                                        mediaButtons.getChildren().clear();
//                                        mediaButtons.getChildren().removeAll();
                                        mediaButtons.getChildren().addAll(previous,pause,next);  
                                        
                                        mediaPlayers[m].setVolume(volSlider.getValue()/100.0);
                                        album.setText(Album[m]);
                                        title.setText(Title[m]);
                                        artist.setText(Artist[m]);
                                        year.setText(Year[m]);
                                        genre.setText(Genre[m]);
                                        albumCover.setImage(AlbumCover[m]);
                                        length.setText(Length[m]);
                                        
                                        //********//
                                        durations[k]=mediaPlayers[k].getMedia().getDuration();
                                        int intDuration=(int)Math.floor(durations[k].toSeconds());
                                        int durationHours=intDuration/3600;
                                        if (durationHours > 0) 
                                        {
                                            intDuration -= durationHours * 60 * 60;
                                        }
                                        int durationMinutes = intDuration / 60;
                                        int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                                        if(durationHours>0)
                                        {
                                            Length[k]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                                        }
                                        else 
                                        {
                                            Length[k]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                                            System.out.println(durations[k]);
                                        }
                                        length.setText(Length[k]);
                                        
                                    }
                                    else
                                        if(audio_playing>=0)
                                        {
                                        mediaPlayers[k].stop();
                                        }
                            }
                            
                            }
                    }
                }
            });
        }
        
//****************************************************************************//
                        
//*****************************MEDIA STATUS*****************************************//
       for(int i=0;i<songs;i++)
       {
           final int k=i;
           
              mediaPlayers[i].setOnPlaying(new Runnable()
              {
                  @Override
                  public void run() 
                  {
                      duration=mediaPlayers[k].getMedia().getDuration();
                  }
              });
       }
        
        /*
       for(int i=0;i<countSongs();i++)
       {
           final int a=i;*/
       if(songs>0)
       {
               mediaPlayers[audio_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       try
                       {
                       Duration currTime=mediaPlayers[audio_playing].getCurrentTime();
                       progressbar.setValue(100.0*currTime.divide(durations[audio_playing]).toMillis());
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                           audio_playing=0;
                           mediaButtons.getChildren().clear();
                           mediaButtons.getChildren().addAll(previous,play,next);  
                       }
                       
                    }
                   
               });
       
       //}
       }
       progressbar.valueProperty().addListener(new InvalidationListener()
       {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       if(progressbar.isValueChanging())
                       {
                           mediaPlayers[audio_playing].seek(durations[audio_playing].multiply(progressbar.getValue()/100.0));
                       }
                   }
               });
           
               progressbar.valueProperty().addListener(new ChangeListener<Number>()
               {
                   @Override
                   public void changed(ObservableValue<?extends Number> ob, Number old_val, Number new_val)
                   {
                       progBar.setProgress(new_val.doubleValue()/100);
//                        mediaPlayer.seek(duration.multiply(progressbar.getValue()/100));
                   }
               });
                
//*******************************CHANGE VOLUME********************************************//
               for(int i=0;i<songs;i++)
               {
                   final int a=i;
                   volSlider.valueProperty().addListener(new InvalidationListener()
                   {
                       public void invalidated(Observable ov)
                       {
                           if(volSlider.isValueChanging())
                           {
                               mediaPlayers[a].setVolume(volSlider.getValue()/100.0);
                           }
                       }
                   });
               }
               
               
//*****************************************************************************************//

               
//********************************PLAY_PAUSE audio*******************************************//
               
        play.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                try
                {
                mediaPlayers[audio_playing].play();
                audio_selected=audio_playing;
                mediaPlayers[audio_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       try
                       {
                       Duration currTime=mediaPlayers[audio_playing].getCurrentTime();
                       progressbar.setValue(100.0*currTime.divide(durations[audio_playing]).toMillis());
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                           audio_playing=0;
                           mediaButtons.getChildren().clear();
                           mediaButtons.getChildren().addAll(previous,play,next);  
                       }
                    }
                   
               });
                for(int k=0;k<100;k++)
                {
                    songList[k].setId("audio_list");
                }
                songList[audio_playing].setId("song_clicked");
                mediaPlayers[audio_playing].setVolume(volSlider.getValue()/100.0);
                album.setText(Album[audio_playing]);
                title.setText(Title[audio_playing]);
                artist.setText(Artist[audio_playing]);
                year.setText(Year[audio_playing]);
                genre.setText(Genre[audio_playing]);
                albumCover.setImage(AlbumCover[audio_playing]);
                length.setText(Length[audio_playing]);
                
                //********//
                durations[audio_playing]=mediaPlayers[audio_playing].getMedia().getDuration();
                int intDuration=(int)Math.floor(durations[audio_playing].toSeconds());
                int durationHours=intDuration/3600;
                if (durationHours > 0) 
                {
                    intDuration -= durationHours * 60 * 60;
                }
                int durationMinutes = intDuration / 60;
                int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                if(durationHours>0)
                {
                    Length[audio_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                }
                else 
                {
                    Length[audio_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                    System.out.println(durations[audio_playing]);
                }
                length.setText(Length[audio_playing]);
                
                
               mediaButtons.getChildren().removeAll(previous,play,next);
               mediaButtons.getChildren().addAll(previous,pause,next);  
            }
                catch(Exception ex)
                {
                    System.out.println(ex);
                }
            }
        });
        
        pause.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                mediaPlayers[audio_playing].pause();
               //mediaPlayer.pause();
               mediaButtons.getChildren().removeAll(previous,pause,next);
               mediaButtons.getChildren().addAll(previous,play,next);  
            }
        });
               
               
        previous.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent t) 
            {
                if(audio_playing>=0)
                {
                    if(mediaPlayers[audio_playing].getStatus()==Status.PLAYING)
                    {
                        mediaPlayers[audio_playing].stop();
                    }
                audio_playing--;
                if(audio_playing<0)
                {
                    audio_playing++;
                    mediaButtons.getChildren().clear();
                    mediaButtons.getChildren().addAll(previous, play, next);
                }
                else
                {
                mediaPlayers[audio_playing].play();
                mediaPlayers[audio_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       Duration currTime=mediaPlayers[audio_playing].getCurrentTime();
                       progressbar.setValue(100.0*currTime.divide(durations[audio_playing]).toMillis());
                    }
                   
               });
                for(int k=0;k<100;k++)
                {
                    songList[k].setId("audio_list");
                }
                songList[audio_playing].setId("song_clicked");
                mediaPlayers[audio_playing].setVolume(volSlider.getValue()/100.0);
                album.setText(Album[audio_playing]);
                title.setText(Title[audio_playing]);
                artist.setText(Artist[audio_playing]);
                year.setText(Year[audio_playing]);
                genre.setText(Genre[audio_playing]);
                albumCover.setImage(AlbumCover[audio_playing]);
                length.setText(Length[audio_playing]);
                
                //********//
                durations[audio_playing]=mediaPlayers[audio_playing].getMedia().getDuration();
                int intDuration=(int)Math.floor(durations[audio_playing].toSeconds());
                int durationHours=intDuration/3600;
                if (durationHours > 0) 
                {
                    intDuration -= durationHours * 60 * 60;
                }
                int durationMinutes = intDuration / 60;
                int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                if(durationHours>0)
                {
                    Length[audio_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                }
                else 
                {
                    Length[audio_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                    System.out.println(durations[audio_playing]);
                }
                length.setText(Length[audio_playing]);
                }
            }
            }
        });
        
        
        next.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent t) 
            {
                if(audio_playing>=0)
                {
                    if(mediaPlayers[audio_playing].getStatus()==Status.PLAYING)
                    {
                        mediaPlayers[audio_playing].stop();
                    }
                audio_playing++;
                if(audio_playing>=songs)
                {
                    audio_playing--;
                    mediaButtons.getChildren().clear();
                    mediaButtons.getChildren().addAll(previous, play, next);
                }
                else
                {
                mediaPlayers[audio_playing].play();
                mediaPlayers[audio_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       Duration currTime=mediaPlayers[audio_playing].getCurrentTime();
                       progressbar.setValue(100.0*currTime.divide(durations[audio_playing]).toMillis());
                    }
                   
               });
                for(int k=0;k<100;k++)
                {
                    songList[k].setId("audio_list");
                }
                songList[audio_playing].setId("song_clicked");
                mediaPlayers[audio_playing].setVolume(volSlider.getValue()/100.0);
                album.setText(Album[audio_playing]);
                title.setText(Title[audio_playing]);
                artist.setText(Artist[audio_playing]);
                year.setText(Year[audio_playing]);
                genre.setText(Genre[audio_playing]);
                albumCover.setImage(AlbumCover[audio_playing]);
                length.setText(Length[audio_playing]);
                
                //********//
                durations[audio_playing]=mediaPlayers[audio_playing].getMedia().getDuration();
                int intDuration=(int)Math.floor(durations[audio_playing].toSeconds());
                int durationHours=intDuration/3600;
                if (durationHours > 0) 
                {
                    intDuration -= durationHours * 60 * 60;
                }
                int durationMinutes = intDuration / 60;
                int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                if(durationHours>0)
                {
                    Length[audio_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                }
                else 
                {
                    Length[audio_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                    System.out.println(durations[audio_playing]);
                }
                length.setText(Length[audio_playing]);
                }
            }
            }
        });
                
 //*****************************Audio Scene ReSize********************************//       
        
        audioPlayer.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<?extends Number> ob,Number old,Number new_val)
            {
                if(audioListExpanded.get())
                {
                    AnchorPane.setLeftAnchor(volume, ((audioPlayer.getWidth()-250)/2)-150);
                    AnchorPane.setLeftAnchor(mediaButtons, ((audioPlayer.getWidth()-250)/2)-90);
                    progBar.setMinWidth(audioPlayer.getWidth()-290);
                    progressbar.setMinWidth(audioPlayer.getWidth()-290);
                    progBar.setMaxWidth(audioPlayer.getWidth()-290);
                    progressbar.setMaxWidth(audioPlayer.getWidth()-290);
                    AnchorPane.setLeftAnchor(albumCover, (audioPlayer.getWidth()-250)/2-200);
                    AnchorPane.setLeftAnchor(artist, (audioPlayer.getWidth()-250)/2+30);
                    AnchorPane.setLeftAnchor(album, (audioPlayer.getWidth()-250)/2+30);
                    AnchorPane.setLeftAnchor(year, (audioPlayer.getWidth()-250)/2+30);
                    AnchorPane.setLeftAnchor(genre, (audioPlayer.getWidth()-250)/2+30);
                    AnchorPane.setLeftAnchor(length, (audioPlayer.getWidth()-250)/2+30);  
                    audioScroll.setPrefSize(audioListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);
                }
                else
                {
                    AnchorPane.setLeftAnchor(volume, audioPlayer.getWidth()/2+100);
                    AnchorPane.setLeftAnchor(mediaButtons, (audioPlayer.getWidth()/2)-90);
                    progBar.setMinWidth(audioPlayer.getWidth()-40);
                    progressbar.setMinWidth(audioPlayer.getWidth()-40);
                    progBar.setMaxWidth(audioPlayer.getWidth()-40);
                    progressbar.setMaxWidth(audioPlayer.getWidth()-40);
                    AnchorPane.setLeftAnchor(showProgress, 20.0);
                    AnchorPane.setLeftAnchor(albumCover, audioPlayer.getWidth()/2-100);
                    AnchorPane.setLeftAnchor(artist, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(album, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(year, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(genre, audioPlayer.getWidth()/2+130);
                    AnchorPane.setLeftAnchor(length, audioPlayer.getWidth()/2+130);
                    audioScroll.setPrefSize(audioListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);
                }
            }

        }
        );
        audioPlayer.heightProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<?extends Number> ob,Number old,Number new_val)
            {
                if(audioListExpanded.get())
                {
                   AnchorPane.setLeftAnchor(volume, ((audioPlayer.getWidth()-250)/2)-150);
                   AnchorPane.setLeftAnchor(mediaButtons, ((audioPlayer.getWidth()-250)/2)-90);
                   progBar.setMinWidth(audioPlayer.getWidth()-290);
                   progressbar.setMinWidth(audioPlayer.getWidth()-290);
                   progBar.setMaxWidth(audioPlayer.getWidth()-290);
                   progressbar.setMaxWidth(audioPlayer.getWidth()-290);
                   AnchorPane.setLeftAnchor(albumCover, (audioPlayer.getWidth()-250)/2-200);
                   AnchorPane.setLeftAnchor(artist, (audioPlayer.getWidth()-250)/2+20);
                   AnchorPane.setLeftAnchor(album, (audioPlayer.getWidth()-250)/2+20);
                   AnchorPane.setLeftAnchor(year, (audioPlayer.getWidth()-250)/2+20);
                   AnchorPane.setLeftAnchor(genre, (audioPlayer.getWidth()-250)/2+20);
                   AnchorPane.setLeftAnchor(length, (audioPlayer.getWidth()-250)/2+20);
                   audioScroll.setPrefSize(audioListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);
                }
                
                AnchorPane.setTopAnchor(albumCover, audioPlayer.getHeight()/2-120);
                AnchorPane.setTopAnchor(artist, audioPlayer.getHeight()/2-120);
                AnchorPane.setTopAnchor(album, audioPlayer.getHeight()/2-100);
                AnchorPane.setTopAnchor(year, audioPlayer.getHeight()/2-80);
                AnchorPane.setTopAnchor(genre, audioPlayer.getHeight()/2-60);
                AnchorPane.setTopAnchor(length, audioPlayer.getHeight()/2-40);
                reflection.setFraction(audioPlayer.getHeight()*0.001);
                audioClipRect.setHeight(audioPlayer.getHeight());
                audioListPane.setPrefHeight(audioPlayer.getHeight());
                audioScroll.setPrefSize(audioListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);
            }

        }                       
        );        
        
        
//*******************************************************************************************//
        
 //***********************************CODING FOR VIDEOS******************************************//
        
 //*******************************************************************************************//
 
 //*****************************Video PLAYER OPEN*************************//        
        
        final Slider video_progressbar=new Slider();
        video_progressbar.setMax(100);
        video_progressbar.setMinWidth(670);
        video_progressbar.setMaxWidth(670);
        video_progressbar.setBlockIncrement(10);
        
        final ProgressBar video_progBar=new ProgressBar(0);
        video_progBar.setMinWidth(670);
        video_progBar.setMaxWidth(670);
        video_progBar.prefHeight(5);
        video_progBar.getStyleClass().add("progress");
        
        final StackPane video_showProgress=new StackPane();
        video_showProgress.getChildren().addAll(video_progBar,video_progressbar);
//        playerPane.add(showProgress, 0, 40,10,1);
        AnchorPane.setBottomAnchor(video_showProgress, 80.0);
//        AnchorPane.setRightAnchor(showProgress, 20.0);
        AnchorPane.setLeftAnchor(video_showProgress, audioPlayer.getWidth()/2-345);
        
        
        final HBox videoButtons=new HBox(10);
        videoButtons.setAlignment(Pos.CENTER);
                
        final Button videoPlay=new Button("\u25B6");
        videoPlay.setId("mediaPlay");
              
        final Button videoPause=new Button("||");
        videoPause.setId("mediaPlay");
        
        final Button videoNext=new Button("\u23e9");
        videoNext.setId("mediaNext");
        
        final Button videoPrevious=new Button("\u23ea");
        videoPrevious.setId("mediaNext");
          
        final Label video_length=new Label();
        final Label videoCur_length=new Label();
        
        //AnchorPane.setLeftAnchor(video_length, 20.0);
        //AnchorPane.setBottomAnchor(video_length, 40.0);
        
        AnchorPane.setLeftAnchor(videoCur_length, 20.0);
        AnchorPane.setBottomAnchor(videoCur_length, 40.0);
        
        //videoAnchor.getChildren().add(video_length);
        videoAnchor.getChildren().add(videoCur_length);
        
        videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);
        
        AnchorPane.setBottomAnchor(videoButtons, 20.0);
        AnchorPane.setLeftAnchor(videoButtons, (audioPlayer.getWidth()/2)-90);
                
        final Slider video_volSlider=new Slider();
        video_volSlider.setMax(100);
        video_volSlider.setMinWidth(100);
        video_volSlider.setMaxWidth(100);
        video_volSlider.setValue(20);
        
        final ProgressBar video_volProgress=new ProgressBar(0);
        video_volProgress.setMinWidth(100);
        video_volProgress.setMaxWidth(100);
        video_volProgress.prefHeight(5);
        video_volProgress.getStyleClass().add("progress");
        video_volProgress.setProgress(0.2);
        
        final StackPane videoVolume=new StackPane();
        videoVolume.getChildren().addAll(video_volProgress,video_volSlider);
        HBox.setHgrow(video_volProgress, Priority.ALWAYS);
        HBox.setHgrow(video_volSlider, Priority.ALWAYS);
        
        videoAnchor.getChildren().addAll(video_showProgress,videoButtons,videoVolume);
        AnchorPane.setBottomAnchor(videoVolume, 40.0);
        AnchorPane.setRightAnchor(videoVolume, audioPlayer.getWidth()/2-270);
        
        video_volSlider.valueProperty().addListener(new ChangeListener<Number>()
                {
                    @Override
                    public void changed(ObservableValue<?extends Number> ob, Number old_val, Number new_val)
                    {
                        video_volProgress.setProgress(new_val.doubleValue()/100);
                    }
                });

        /*Media videoM=new Media("http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv");
        MediaPlayer videoMP=new MediaPlayer(videoM);
        videoMP.play();
        */
        
        mediaView.setFitHeight(350.0);
        mediaView.setFitWidth(670.0);
        mediaView.setPreserveRatio(false);
        
        AnchorPane.setTopAnchor(mediaView, 50.0);
        AnchorPane.setLeftAnchor(mediaView, 10.0);
        
        videoAnchor.getChildren().add(mediaView);
//*********************************************************************//
        
        
//******************************Video List****************************************//

                videoListPane=new HBox();
		videoListPane.setPrefSize(videoListRect.getWidth(), videoListRect.getHeight());
		
		final StackPane videoSp1 = new StackPane();
		
		videoSp1.setAlignment(Pos.TOP_LEFT);
		videoSp1.setStyle("-fx-background-color:#ead2f7;-fx-background-insets:0,1.5;-fx-opacity:.92;-fx-background-radius:0px 0px 0px 5px;");
		videoSp1.setPrefSize(videoListRect.getWidth(), videoListRect.getHeight()-30.0);
		
		
		StackPane videoSp2 = new StackPane();
		videoSp2.setPrefSize(30.0, 30.0);
		videoSp2.getChildren().add(videoList);
		videoSp2.setStyle("-fx-cursor:hand;-fx-background-color:#CC99CC;-fx-border-width:1px 0px 1px 1px;-fx-border-color:#333333;-fx-opacity:.92;-fx-border-radius:5px 0px 0px 5px;-fx-background-radius:5px 0px 0px 5px;");
		videoSp2.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent paramT) {
				if(videoListExpanded.get()){
			videoListExpanded.set(false);
		}else{
			videoListExpanded.set(true);
		}
			}
		});
                videoListPane.getChildren().addAll(videoSp1,videoSp2);
//		audioListPane.getChildren().addAll(GroupBuilder.create().children(sp1).build(),GroupBuilder.create().children(sp2).build());
		AnchorPane.setRightAnchor(videoListPane, -(videoListRect.getWidth()-18));
                AnchorPane.setTopAnchor(videoListPane, 0.0);
                videoAnchor.getChildren().add(videoListPane);
                
                
		/* Initial position setting for Top Pane*/
		videoClipRect = new Rectangle();
                videoClipRect.setWidth(30.0);
		videoClipRect.setHeight(videoListRect.getHeight());
		videoClipRect.translateXProperty().set(videoListRect.getWidth()-15.0);
		videoListPane.setClip(videoClipRect);
		videoListPane.translateXProperty().set(-(videoListRect.getWidth()-15.0));
			
		/* Animation for bouncing effect. */
		final Timeline timeDown1 = new Timeline();
		timeDown1.setCycleCount(2);
		timeDown1.setAutoReverse(true);
		final KeyValue keyv1 = new KeyValue(videoClipRect.widthProperty(), (videoListRect.getWidth()-15));
		final KeyValue keyv2 = new KeyValue(videoClipRect.translateXProperty(), 15);
		final KeyValue keyv3 = new KeyValue(videoListPane.translateXProperty(), -(videoListRect.getWidth()-30.0));
		final KeyFrame keyf1 = new KeyFrame(Duration.millis(200), keyv1, keyv2, keyv3);
		timeDown1.getKeyFrames().add(keyf1);
		
		/* Event handler to call bouncing effect after the scroll down is finished. */
		EventHandler<ActionEvent> videoOnFinished = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	timeDown1.play();
            }
        };
        
        videoListLeft = new Timeline();
        videoListRight = new Timeline();
        
        /* Animation for scroll down. */
		videoListLeft.setCycleCount(1);
		videoListLeft.setAutoReverse(true);
		final KeyValue keyvDwn1 = new KeyValue(videoClipRect.widthProperty(), videoListRect.getWidth());
		final KeyValue keyvDwn2 = new KeyValue(videoClipRect.translateXProperty(), 0);
		final KeyValue keyvDwn3 = new KeyValue(videoListPane.translateXProperty(), -(videoListRect.getWidth()-15.0));
		final KeyFrame keyfDwn = new KeyFrame(Duration.millis(400), videoOnFinished, keyvDwn1, keyvDwn2, keyvDwn3);
		videoListLeft.getKeyFrames().add(keyfDwn);
		
		/* Animation for scroll up. */
		videoListRight.setCycleCount(1); 
		videoListRight.setAutoReverse(true);
		final KeyValue keyvUp1 = new KeyValue(videoClipRect.widthProperty(), 15.0);
		final KeyValue keyvUp2 = new KeyValue(videoClipRect.translateXProperty(), videoListRect.getWidth()-15.0);
		final KeyValue keyvUp3 = new KeyValue(videoListPane.translateXProperty(), -(videoListRect.getWidth()-15.0));
		final KeyFrame keyfUp = new KeyFrame(Duration.millis(400), keyvUp1, keyvUp2, keyvUp3);
		videoListRight.getKeyFrames().add(keyfUp);
                
                
                videoListExpanded.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> paramObservableValue,Boolean paramT1, Boolean paramT2) {
				if(paramT2){
					// To expand
                                        videoListLeft.play();
					videoList.setGraphic(rightVideoArrow);
                                        
                                        AnchorPane.setLeftAnchor(videoVolume, ((audioPlayer.getWidth()-250)/2)-100);
                                        AnchorPane.setLeftAnchor(videoButtons, ((audioPlayer.getWidth()-250)/2)-90);
                                        video_progBar.setMinWidth(audioPlayer.getWidth()-290);
                                        video_progressbar.setMinWidth(audioPlayer.getWidth()-290);
                                        video_progBar.setMaxWidth(audioPlayer.getWidth()-290);
                                        video_progressbar.setMaxWidth(audioPlayer.getWidth()-290);
                                        mediaView.setFitWidth(audioPlayer.getWidth()-videoListRect.getWidth()-20.0);
                                        
				}else{
					// To close
                                        videoListRight.play();
					videoList.setGraphic(leftVideoArrow);
                                        
                                        AnchorPane.setLeftAnchor(videoVolume, audioPlayer.getWidth()/2+100);
                                        AnchorPane.setLeftAnchor(videoButtons, (audioPlayer.getWidth()/2)-90);
                                        video_progBar.setMinWidth(audioPlayer.getWidth()-40);
                                        video_progressbar.setMinWidth(audioPlayer.getWidth()-40);
                                        video_progBar.setMaxWidth(audioPlayer.getWidth()-40);
                                        video_progressbar.setMaxWidth(audioPlayer.getWidth()-40);
                                        AnchorPane.setLeftAnchor(video_showProgress, 20.0);
                                        mediaView.setFitWidth(audioPlayer.getWidth()-40.0);
				}
			}
		});
                
               
//*****************************************************************************//


//******************LIST OF VIDEO*******************************//
        
        final VBox videoListBox=new VBox();
        final ScrollPane videoScroll=new ScrollPane(); 
        
        AnchorPane videoListAnchor=new AnchorPane();
        final Button videoList[]=new Button[100];
//        File songFile[]=new File[100];
        final File videoFile[] = new File[100];
        int[] videoID=new int[100];
        
        final Media videoMedia[]=new Media[100];
        final MediaPlayer videoMP[]=new MediaPlayer[100];
        int vid=0;
        for(int i=0;i<100;i++)
        {
            videoList[i]=new Button();
            videoList[i].setId("audio_list");
            videoList[i].setMinWidth(230.0);
            videoList[i].setMaxWidth(230.0);
        }
        
        if(username!=null)
        {
        try
        {
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        con=DriverManager.getConnection("jdbc:odbc:mydr");
        prepare=con.prepareStatement("select * from "+username.toLowerCase()+"Video");
        result=prepare.executeQuery();
        
        String video_name;
        while(result.next())
        {
           videoID[vid]=Integer.parseInt(result.getString(1));
            //System.out.println(result.getString(2));
            video_name=result.getString(2);
            videoList[vid].setText(video_name);
//            songFile[j]=new File(result.getString(3));*/
            //videoList[vid].setText("Chhota Bheem Old enemies 3");
            //videoFile[vid]=new File("c://mani//files//soaps//only you//Chhota Bheem Old enemies 3.flv");
            videoFile[vid]=new File(result.getString(3));
            //System.out.println(result.getString(3));
            videoMedia[vid]=new Media(videoFile[vid].toURI().toURL().toString());
            videoMP[vid]=new MediaPlayer(videoMedia[vid]);
            videoListBox.getChildren().add(videoList[vid]);
            //videoListAnchor.getChildren().add(videoList[vid]);
            vid++;
            videos++;
            video_playing=0;
        }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        }
        videoScroll.setContent(videoListBox);
        videoScroll.setPrefSize(videoListRect.getWidth()-15.0, videoListRect.getHeight()-150.0);
        final Button addVideo=new Button("Add Video...");
        AnchorPane.setBottomAnchor(addVideo, 10.0);
        AnchorPane.setTopAnchor(videoScroll, 50.0);
        
        //videoListAnchor.getChildren().addAll(videoScroll, addVideo);
        
        
        videoSp1.getChildren().addAll(videoListAnchor);
        
        
        //*****************************************//
        
        
        Text wel3=new Text("Welcome, ");
        AnchorPane.setTopAnchor(wel3,10.0);
        AnchorPane.setLeftAnchor(wel3, 10.0);
        
        final Button wel4=new Button(username);
        AnchorPane.setTopAnchor(wel4,8.0);
        AnchorPane.setLeftAnchor(wel4, 55.0);
        wel4.setId("user");
        
        final ContextMenu vid_userInfo=new ContextMenu();
        MenuItem vid_prof=new MenuItem("Edit Password");
        
        MenuItem vid_logout=new MenuItem("Logout");
        
        vid_userInfo.getItems().addAll(vid_prof, vid_logout);
        
        videoListAnchor.getChildren().addAll(videoScroll, addVideo,wel3,wel4);
        
        wel4.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent e)
                {
                    if(e.getButton().equals(MouseButton.SECONDARY))
                    {
                        wel4.setContextMenu(vid_userInfo);
                    }
                }
            });
            
        vid_logout.setOnAction(new EventHandler<ActionEvent>()
        {
                    public void handle(ActionEvent t) 
                    {
                        username="";
                        userTextField.setText("");
                        pwBox.setText("");
                        primaryStage.setScene(scene);
                        actiontarget.setText("");
                    }
            
        }
        );
        
      //  sp1.getChildren().addAll(audioListAnchor);
        
        //*****************************************//
        
        
 //***********************************************************************************************//       
 
//*******************************Video Chooser**********************************//
        
        final FileChooser videoSelect=new FileChooser();
        videoSelect.setTitle("Add Videos");
        videoSelect.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Supported Movies", "*.mp4","*.flv"),new FileChooser.ExtensionFilter("MP4", "*.mp4"),new FileChooser.ExtensionFilter("FLV Videos", "*.flv"));
        
        addVideo.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                //if(video_playing>=0)
                //videoMP[video_playing].pause();
/*                for(int i=0;i<countSongs();i++)
                {
                    if(mediaPlayers[i].getStatus()==Status.PLAYING)
                    {
                        mediaPlayers[i].stop();
                        break;
                    }
                }*/
                List<File> newVideos=videoSelect.showOpenMultipleDialog(primaryStage);
                if(newVideos!=null)
                {
                    int b=0;
                    for(File videofile:newVideos)
                    {
                        
                        try
                        {
                            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                            con=DriverManager.getConnection("jdbc:odbc:mydr");
                            prepare=con.prepareStatement("insert into "+username.toLowerCase()+"Video values(?,?,?)");
                            prepare.setInt(1,videos+1);
                            prepare.setString(2, videofile.getName());
                            prepare.setString(3, videofile.getPath());
                            prepare.executeUpdate();
                            videos++;
                         }
                        catch(Exception ex)
                        {
                            System.out.println(ex);
                        }   
                        
                        try
                        {
                            int id;
                            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                            con=DriverManager.getConnection("jdbc:odbc:mydr");
                            prepare=con.prepareStatement("select * from "+username.toLowerCase()+"Video");
                            result=prepare.executeQuery();
                            while(result.next())
                            {
                                id=result.getInt(1)-1;
                                videoList[id].setText(result.getString(2));
                                videoFile[id]=new File(result.getString(3));
                                videoMedia[id]=new Media(videoFile[id].toURI().toURL().toString());
                                videoMP[id]=new MediaPlayer(videoMedia[id]);
                                videoListBox.getChildren().remove(videoList[id]);
                                videoListBox.getChildren().add(videoList[id]);
                                if(video_playing<0)
                                {
                                    video_playing=0;
                                }
                            }
                        }
                        catch(Exception ex)
                        {
                            System.out.println(ex);
                        }
                        //openSongs(songfile,s);
                        
        for(int i=0;i<videos;i++)
        {
        videoList[i].setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(final KeyEvent t) 
            {
                
            if( t.getCode().equals(KeyCode.DELETE))
            {
                try
                       {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("delete from "+username.toLowerCase()+"Video where videoID="+(video_selected+1));
                           prepare.executeUpdate();
                           videos--;
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                       }
                       try
                       {
                           for(int i=video_selected;i<=videos;i++)
                           {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("update "+username.toLowerCase()+"Video set videoID="+i+"where name=");
                           prepare.executeUpdate();
                           
                           videoList[i].setText(videoList[i+1].getText());
                           videoFile[i]=new File(videoFile[i+1].getPath());
                           videoMedia[i]=new Media(videoFile[i].toURI().toURL().toString());
                           videoMP[i]=new MediaPlayer(videoMedia[i]);
                           }
                           videoList[video_selected].setText("");
                           videoFile[video_selected]=new File("");
                           videoMedia[video_selected]=new Media(videoFile[video_selected].toURI().toURL().toString());
                           videoMP[video_selected]=new MediaPlayer(videoMedia[video_selected]);
                           video_selected--;
                           videoList[video_selected].setId("song_clicked");
                       }
                       catch(Exception e)
                       {
                           System.out.println();
                       }
                       videoListBox.getChildren().remove(videoList[videos]);
            } 
            }
        });
        } 
        
                    }
                }
                if(video_playing>0)
                {
                //videoMP[video_playing].play();
                //mediaView.setMediaPlayer(videoMP[video_playing]);
                videoMP[video_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       Duration currTime=videoMP[video_playing].getCurrentTime();
                       video_progressbar.setValue(100.0*currTime.divide(videoDuration).toMillis());
                    }
                   
               });
                }
            }
        });
        
//****************************************************************************//
        
//******************************DURATION FOR VIDEOS******************************************//
        final String videoLength[]=new String[100];
        for(int i=0;i<videos;i++)
        {            videoLength[i]=new String();
        }
        
        
        final Duration videoDurations[]=new Duration[100];
        for(int i=0;i<videos;i++)
        {
           final int k=i;
           
           videoMP[k].setOnEndOfMedia(new Runnable()
                   {
                     public void run()  
                     {
                         videoMP[k].stop();
                         video_playing++;
                         if(video_playing>=videos)
                         {
                             video_playing--;
                             videoButtons.getChildren().clear();
                             videoButtons.getChildren().addAll(videoPrevious, videoPlay, videoNext);
                         }
                         else
                         {
                             videoMP[video_playing].play();
                             mediaView.setMediaPlayer(videoMP[video_playing]);
                             videoMP[video_playing].currentTimeProperty().addListener(new InvalidationListener()
                             {
                                 @Override
                                 public void invalidated(Observable ob)
                                 {
                                     Duration currTime=videoMP[video_playing].getCurrentTime();
                                     video_progressbar.setValue(100.0*currTime.divide(videoDuration).toMillis());
                                 }
                                 
                             });
                             for(int k=0;k<100;k++)
                             {
                                 videoList[k].setId("audio_list");
                             }
                             videoList[video_playing].setId("song_clicked");
                             videoMP[video_playing].setVolume(video_volSlider.getValue()/100.0);
                             
                             //********//
                             videoDurations[video_playing]=videoMP[video_playing].getMedia().getDuration();
                             int intDuration=(int)Math.floor(videoDurations[video_playing].toSeconds());
                             int durationHours=intDuration/3600;
                             if (durationHours > 0) 
                             {
                                 intDuration -= durationHours * 60 * 60;
                             }
                             int durationMinutes = intDuration / 60;
                             int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                             if(durationHours>0)
                             {
                                 videoLength[video_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                             }
                             else 
                             {
                                 videoLength[video_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                                 System.out.println(videoDurations[video_playing]);
                             }
                             video_length.setText(videoLength[video_playing]);
                         }
                     }
                   });
           //durations[k]=mediaPlayers[i].getMedia().getDuration();
           videoMP[k].setOnPlaying(new Runnable()
//            mediaPlayers[k].setOnReady(new Runnable()
           {
               @Override
               public void run() 
               {
                   videoDurations[k]=videoMP[k].getMedia().getDuration();
                   int intDuration=(int)Math.floor(videoDurations[k].toSeconds());
                   int durationHours=intDuration/3600;
                   if (durationHours > 0) 
                   {
                    intDuration -= durationHours * 60 * 60;
                   }
                   int durationMinutes = intDuration / 60;
                   int durationSeconds = intDuration; //- durationHours * 60 * 60 -durationMinutes * 60;
                   if(durationHours>0)
                   {
                       videoLength[k]=String.format("%d:%02d:02d",durationHours, durationMinutes, durationSeconds);
                   }
                   else 
                   {
                       videoLength[k]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                       System.out.println(videoDurations[k]);
                   }
           }
            });
        }
                 
      
       
//*********************************************************************************************//        

//***********************************Delete Video*********************************************//
               
               final ContextMenu video_delete=new ContextMenu();
               
               MenuItem videoDelete=new MenuItem("Delete");
               video_delete.getItems().add(videoDelete);
               videoDelete.setOnAction(new EventHandler()
               {
                   int n=video_selected;
                   public void handle(Event t) 
                   {
                       try
                       {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("delete from "+username.toLowerCase()+"Video where videoID="+(video_selected+1));
                           prepare.executeUpdate();
                           n--;
                           videos--;
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                       }
                       try
                       {
                           for(int i=video_selected;i<=videos;i++)
                           {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("update "+username.toLowerCase()+"Video set videoID="+i+"where videoID="+(i+1));
                           prepare.executeUpdate();
                           
                           videoList[i].setText(videoList[i+1].getText());
                           videoFile[i]=new File(videoFile[i+1].getPath());
                           videoMedia[i]=new Media(videoFile[i].toURI().toURL().toString());
                           videoMP[i]=new MediaPlayer(videoMedia[i]);
                           }
                           video_selected--;
                           videoList[video_selected].setId("song_clicked");
                       }
                       catch(Exception e)
                       {
                           System.out.println();
                       }
                       videoListBox.getChildren().remove(videoList[videos]);
                   }
                   
               });
               
               
//********************************************************************************************//


//***************************************************************************/
        
        for(int i=0;i<videos;i++)
        {
        videoList[i].setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(final KeyEvent t) 
            {
                
            if( t.getCode().equals(KeyCode.DELETE))
            {
                try
                       {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("delete from "+username.toLowerCase()+"Video where videoID="+(video_selected+1));
                           prepare.executeUpdate();
                           videos--;
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                       }
                       try
                       {
                           for(int i=video_selected;i<=videos;i++)
                           {
                           Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                           con=DriverManager.getConnection("jdbc:odbc:mydr");
                           prepare=con.prepareStatement("update "+username.toLowerCase()+"Video set videoID="+i+"where name=");
                           prepare.executeUpdate();
                           
                           videoList[i].setText(videoList[i+1].getText());
                           videoFile[i]=new File(videoFile[i+1].getPath());
                           videoMedia[i]=new Media(videoFile[i].toURI().toURL().toString());
                           videoMP[i]=new MediaPlayer(videoMedia[i]);
                           }
                           videoList[video_selected].setText("");
                           videoFile[video_selected]=new File("");
                           videoMedia[video_selected]=new Media(videoFile[video_selected].toURI().toURL().toString());
                           videoMP[video_selected]=new MediaPlayer(videoMedia[video_selected]);
                           video_selected--;
                           videoList[video_selected].setId("song_clicked");
                       }
                       catch(Exception e)
                       {
                           System.out.println();
                       }
                       videoListBox.getChildren().remove(videoList[videos]);
            } 
            }
        });
        } 
        
        for(int i=0;i<100;i++)
        {
            final int m=i;
            videoList[i].setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent e)
                { 
                    if(e.getButton().equals(MouseButton.SECONDARY))
                    {
                        videoList[m].setContextMenu(video_delete);
                    }
                    if(e.getButton().equals(MouseButton.PRIMARY))
                    {
                        if(e.getClickCount()==1)
                        {
                            for(int k=0;k<100;k++)
                            {
                                videoList[k].setId("audio_list");
                            }
                            videoList[m].setId("song_clicked");
                            video_selected=m;
                        }
                        if(e.getClickCount()==2)
                        {
                            videoMP[video_playing].stop();
                            video_playing=m;
                            if(video_playing>videos)
                            {
                                video_playing--;
                                videoButtons.getChildren().clear();
                                videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);  
                            }
                else
                {
                videoMP[video_playing].play();
                videoButtons.getChildren().clear();
                videoButtons.getChildren().addAll(videoPrevious,videoPause,videoNext);  
                mediaView.setMediaPlayer(videoMP[video_playing]);
                videoMP[video_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       Duration currTime=videoMP[video_playing].getCurrentTime();
                       video_progressbar.setValue(100.0*currTime.divide(videoDurations[video_playing]).toMillis());
                    }
                   
               });
                for(int k=0;k<100;k++)
                {
                    videoList[k].setId("audio_list");
                }
                videoList[video_playing].setId("song_clicked");
                videoMP[video_playing].setVolume(video_volSlider.getValue()/100.0);
                video_length.setText(videoLength[video_playing]);
                
                //********//
                videoDurations[video_playing]=videoMP[video_playing].getMedia().getDuration();
                int intDuration=(int)Math.floor(videoDurations[video_playing].toSeconds());
                int durationHours=intDuration/3600;
                if (durationHours > 0) 
                {
                    intDuration -= durationHours * 60 * 60;
                }
                int durationMinutes = intDuration / 60;
                int durationSeconds = intDuration /*- durationHours * 60 * 60*/ -durationMinutes * 60;
                if(durationHours>0)
                {
                    videoLength[video_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                }
                else 
                {
                    videoLength[video_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                    System.out.println(videoDurations[video_playing]);
                }
                video_length.setText(videoLength[video_playing]);
                }
                        }

                    }
                }
            });
        }
        
        
//****************************************************************************//
        
//*****************************VIDEo STATUS*****************************************//
       for(int i=0;i<videos;i++)
       {
           final int k=i;
           
              videoMP[i].setOnPlaying(new Runnable()
              {
                  @Override
                  public void run() 
                  {
                      videoDuration=videoMP[k].getMedia().getDuration();
                  }
              });
       }
        
        /*
       for(int i=0;i<countSongs();i++)
       {
           final int a=i;*/
       if(video_playing>=0)
       {
               videoMP[video_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       try
                       {
                       Duration currTime=videoMP[video_playing].getCurrentTime();
                       video_progressbar.setValue(100.0*currTime.divide(videoDurations[video_playing]).toMillis());
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                           video_playing=0;
                           videoButtons.getChildren().clear();
                           videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);  
                       }
                       
                    }
                   
               });
       }
       //}
       
       video_progressbar.valueProperty().addListener(new InvalidationListener()
       {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       if(video_progressbar.isValueChanging())
                       {
                           videoMP[video_playing].seek(videoDurations[video_playing].multiply(video_progressbar.getValue()/100.0));
                       }
                   }
               });
           
               video_progressbar.valueProperty().addListener(new ChangeListener<Number>()
               {
                   @Override
                   public void changed(ObservableValue<?extends Number> ob, Number old_val, Number new_val)
                   {
                       video_progBar.setProgress(new_val.doubleValue()/100);
//                        mediaPlayer.seek(duration.multiply(progressbar.getValue()/100));
                       int intDuration=(int)Math.floor(videoMP[video_playing].getCurrentTime().toSeconds());
                             int durationHours=intDuration/3600;
                             if (durationHours > 0) 
                             {
                                 intDuration -= durationHours * 60 * 60;
                             }
                             int durationMinutes = intDuration / 60;
                             int durationSeconds = intDuration - durationHours * 60 * 60 -durationMinutes * 60;
                             if(durationHours>0)
                             {
                                 videoCur_length.setText(String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds));
                                 video_progressbar.setTooltip(new Tooltip(videoCur_length.getText()+"\nSlide to Seek"));
                             }
                             else 
                             {
                                 videoCur_length.setText(String.format("%02d:%02d",durationMinutes,durationSeconds));
                                 video_progressbar.setTooltip(new Tooltip(videoCur_length.getText()+"\nSlide to Seek"));
                             }
                   }
               });
                
//*******************************CHANGE VIDEO VOLUME********************************************//
               for(int i=0;i<videos;i++)
               {
                   final int a=i;
                   video_volSlider.valueProperty().addListener(new InvalidationListener()
                   {
                       public void invalidated(Observable ov)
                       {
                           if(video_volSlider.isValueChanging())
                           {
                               videoMP[a].setVolume(video_volSlider.getValue()/100.0);
                           }
                       }
                   });
               }
               
               
//*****************************************************************************************//


//********************************PLAY_PAUSE audio*******************************************//
               
        videoPlay.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                videoMP[video_playing].play();
                mediaView.setMediaPlayer(videoMP[video_playing]);
                videoMP[video_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       try
                       {
                       Duration currTime=videoMP[video_playing].getCurrentTime();
                       video_progressbar.setValue(100.0*currTime.divide(videoDurations[video_playing]).toMillis());
                       }
                       catch(Exception e)
                       {
                           System.out.println(e);
                           video_playing=0;
                           videoButtons.getChildren().clear();
                           videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);  
                       }
                    }
                   
               });
                for(int k=0;k<100;k++)
                {
                    videoList[k].setId("audio_list");
                }
                videoList[video_playing].setId("song_clicked");
                videoMP[video_playing].setVolume(video_volSlider.getValue()/100.0);
                video_length.setText(videoLength[video_playing]);
                
                //********//
                videoDurations[video_playing]=videoMP[video_playing].getMedia().getDuration();
                int intDuration=(int)Math.floor(videoDurations[video_playing].toSeconds());
                int durationHours=intDuration/3600;
                if (durationHours > 0) 
                {
                    intDuration -= durationHours * 60 * 60;
                }
                int durationMinutes = intDuration / 60;
                if(durationMinutes>0)
                {
                    intDuration-=durationMinutes*60;
                }
                int durationSeconds = intDuration; //- durationHours * 60 * 60 -durationMinutes * 60;
                if(durationHours>0)
                {
                    videoLength[video_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                }
                else 
                {
                    videoLength[video_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                    System.out.println(videoDurations[video_playing]);
                }
                video_length.setText(videoLength[video_playing]);
                
                
               videoButtons.getChildren().removeAll(videoPrevious,videoPlay,videoNext);
               videoButtons.getChildren().addAll(videoPrevious,videoPause,videoNext);  
            }
        });
        
        videoPause.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                videoMP[video_playing].pause();
               //mediaPlayer.pause();
               videoButtons.getChildren().removeAll(videoPrevious,videoPause,videoNext);
               videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);  
            }
        });
               
               
        videoPrevious.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                if(videoMP[video_playing].getStatus()==Status.PLAYING)
                {
                    videoMP[video_playing].stop();
                }
                video_playing--;
                if(video_playing<0)
                {
                    video_playing++;
                    videoButtons.getChildren().clear();
                    videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);  
                }
                else
                {
                videoMP[video_playing].play();
                videoButtons.getChildren().clear();
                videoButtons.getChildren().addAll(videoPrevious, videoPause, videoNext);
                mediaView.setMediaPlayer(videoMP[video_playing]);
                videoMP[video_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       Duration currTime=videoMP[video_playing].getCurrentTime();
                       video_progressbar.setValue(100.0*currTime.divide(videoDurations[video_playing]).toMillis());
                    }
                   
               });
                for(int k=0;k<100;k++)
                {
                    videoList[k].setId("audio_list");
                }
                videoList[video_playing].setId("song_clicked");
                videoMP[video_playing].setVolume(video_volSlider.getValue()/100.0);
                video_length.setText(videoLength[video_playing]);
                
                //********//
                videoDurations[video_playing]=videoMP[video_playing].getMedia().getDuration();
                int intDuration=(int)Math.floor(videoDurations[video_playing].toSeconds());
                int durationHours=intDuration/3600;
                if (durationHours > 0) 
                {
                    intDuration -= durationHours * 60 * 60;
                }
                int durationMinutes = intDuration / 60;
                int durationSeconds = intDuration /*- durationHours * 60 * 60*/ -durationMinutes * 60;
                if(durationHours>0)
                {
                    videoLength[video_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                }
                else 
                {
                    videoLength[video_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                    System.out.println(videoDurations[video_playing]);
                }
                video_length.setText(videoLength[video_playing]);
                }
            }
            
        });
        
        
        videoNext.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                videoMP[video_playing].stop();
                video_playing++;
                if(video_playing>=videos)
                {
                    video_playing--;
                    videoButtons.getChildren().clear();
                    videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);  
                }
                else
                {                           
                videoMP[video_playing].play();
                videoButtons.getChildren().clear();
                videoButtons.getChildren().addAll(videoPrevious,videoPause,videoNext);  
                mediaView.setMediaPlayer(videoMP[video_playing]);
                videoMP[video_playing].currentTimeProperty().addListener(new InvalidationListener()
               {
                   @Override
                   public void invalidated(Observable ob)
                   {
                       Duration currTime=videoMP[video_playing].getCurrentTime();
                       video_progressbar.setValue(100.0*currTime.divide(videoDuration).toMillis());
                    }
                   
               });
                for(int k=0;k<100;k++)
                {
                    videoList[k].setId("audio_list");
                }
                videoList[video_playing].setId("song_clicked");
                videoMP[video_playing].setVolume(video_volSlider.getValue()/100.0);
                video_length.setText(videoLength[video_playing]);
                
                //********//
                videoDurations[video_playing]=videoMP[video_playing].getMedia().getDuration();
                int intDuration=(int)Math.floor(videoDurations[video_playing].toSeconds());
                int durationHours=intDuration/3600;
                if (durationHours > 0) 
                {
                    intDuration -= durationHours * 60 * 60;
                }
                int durationMinutes = intDuration / 60;
                int durationSeconds = intDuration /*- durationHours * 60 * 60 */-durationMinutes * 60;
                if(durationHours>0)
                {
                    videoLength[video_playing]=String.format("%d:%02d:%02d",durationHours, durationMinutes, durationSeconds);
                }
                else 
                {
                    videoLength[video_playing]=String.format("%02d:%02d",durationMinutes,durationSeconds);
                    System.out.println(videoDurations[video_playing]);
                }
                video_length.setText(videoLength[video_playing]);
                }
            }
            
        });
               
//*********************************************************************************************************//               
               
//***********************************Audio-Video Stop*********************************************//
        
        video.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                mediaPlayers[audio_playing].stop();
                mediaButtons.getChildren().clear();
                primaryStage.setHeight(500);
                primaryStage.setWidth(700);
                mediaButtons.getChildren().addAll(previous,play,next);  
            }          
        });
        
        audio.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent t) 
            {
                videoMP[video_playing].stop();
                videoButtons.getChildren().clear();
                primaryStage.setFullScreen(false);
                primaryStage.setHeight(500);
                primaryStage.setWidth(700);
                videoButtons.getChildren().addAll(videoPrevious,videoPlay,videoNext);  
            }          
        });

        
//******************************Full Screen******************************************************//
        final StackPane root=new StackPane();
        final Scene fullScreen=new Scene(root ,960,540);
        
        //final DoubleProperty width = mediaView.fitWidthProperty();
        //final DoubleProperty height = mediaView.fitHeightProperty();
        if(primaryStage.getScene()!=fullScreen)
        {
        mediaView.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent t) 
            {    
                if(t.getButton().equals(MouseButton.PRIMARY))
                {
                    if(t.getClickCount()==2)
                    {
                        primaryStage.setFullScreen(true);
                        videoAnchor.getChildren().clear();
                        AnchorPane.setTopAnchor(mediaView, 0.0);
                        AnchorPane.setLeftAnchor(mediaView, 0.0);
                        videoAnchor.getChildren().add(mediaView);
                        mediaView.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight()+40.0);
                        mediaView.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth());
                    }
                }
            }
            
        });
        }
        
        audioPlayer.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            public void handle(KeyEvent t) 
            {
                if(t.getCode().equals(KeyCode.ESCAPE))
                {
                    AnchorPane.setTopAnchor(mediaView, 50.0);
                    AnchorPane.setLeftAnchor(mediaView, 10.0);
                    videoAnchor.getChildren().clear();
                    videoAnchor.getChildren().addAll(audio,video,mediaView,video_showProgress,videoButtons,videoVolume,videoListPane);
                }
            }
            
        });
        
//***********************************************************************************************//
  
//*****************************Video Scene ReSize********************************//       
        
        audioPlayer.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<?extends Number> ob,Number old,Number new_val)
            {
                if(videoListExpanded.get())
                {
                    AnchorPane.setLeftAnchor(videoVolume, ((audioPlayer.getWidth()-250)/2)-100);
                    AnchorPane.setLeftAnchor(videoButtons, ((audioPlayer.getWidth()-250)/2)-90);
                    video_progBar.setMinWidth(audioPlayer.getWidth()-290);
                    video_progressbar.setMinWidth(audioPlayer.getWidth()-290);
                    video_progBar.setMaxWidth(audioPlayer.getWidth()-290);
                    video_progressbar.setMaxWidth(audioPlayer.getWidth()-290);
                    videoScroll.setPrefSize(videoListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);
                    mediaView.setFitWidth(audioPlayer.getWidth()-videoListRect.getWidth()-30.0);
                 }
                else
                {
                    AnchorPane.setLeftAnchor(videoVolume, audioPlayer.getWidth()/2+100);
                    AnchorPane.setLeftAnchor(videoButtons, (audioPlayer.getWidth()/2)-90);
                    video_progBar.setMinWidth(audioPlayer.getWidth()-40);
                    video_progressbar.setMinWidth(audioPlayer.getWidth()-40);
                    video_progBar.setMaxWidth(audioPlayer.getWidth()-40);
                    video_progressbar.setMaxWidth(audioPlayer.getWidth()-40);
                    AnchorPane.setLeftAnchor(video_showProgress, 20.0);
                    videoScroll.setPrefSize(videoListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);                    
                    mediaView.setFitWidth(audioPlayer.getWidth()-30.0);
                }   
            }
        }
        );
        audioPlayer.heightProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<?extends Number> ob,Number old,Number new_val)
            {
                if(videoListExpanded.get())
                {
                   AnchorPane.setLeftAnchor(videoVolume, ((audioPlayer.getWidth()-250)/2)-80);
                   AnchorPane.setLeftAnchor(videoButtons, ((audioPlayer.getWidth()-250)/2)-90);
                   video_progBar.setMinWidth(audioPlayer.getWidth()-290);
                   video_progressbar.setMinWidth(audioPlayer.getWidth()-290);
                   video_progBar.setMaxWidth(audioPlayer.getWidth()-290);
                   video_progressbar.setMaxWidth(audioPlayer.getWidth()-290);
                   videoScroll.setPrefSize(videoListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);
                }
                
                videoClipRect.setHeight(audioPlayer.getHeight());
                videoListPane.setPrefHeight(audioPlayer.getHeight());
                videoScroll.setPrefSize(videoListRect.getWidth()-15.0, audioPlayer.getHeight()-150.0);
                mediaView.setFitHeight(audioPlayer.getHeight()-160.0);
                primaryStage.setHeight(audioPlayer.getHeight());
            }

        }                       
        );        
        
                }
            }
        });
//*******************************************************************************************//
        
        
        primaryStage.show();
        
        
    }

    
//**********************************METHODS FOR AUDIO***********************************************//    
    
 /*   public int countSongs()
    {
        int j = 0;
        try
            {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con=DriverManager.getConnection("jdbc:odbc:mydr");
            prepare=con.prepareStatement("select * from "+username+"List");
            result=prepare.executeQuery();
            while(result.next())
            {
                j=result.getInt(1);
            }
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            return j;
    }
    
    public int countVideos()
    {
        int j = 0;
        try
            {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con=DriverManager.getConnection("jdbc:odbc:mydr");
            prepare=con.prepareStatement("select * from "+username+"List");
            result=prepare.executeQuery();
            while(result.next())
            {
                j=result.getInt(1);
            }
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            return j;
    }
    */
//************************************************************************************************//    
    
}
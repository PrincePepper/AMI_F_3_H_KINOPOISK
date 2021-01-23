package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class Controller {

    private static String find_item;
    @FXML
    public JFXComboBox<String> ComboBox;
    @FXML
    public AnchorPane allinfo;
    @FXML
    public ImageView imageViewBackground;
    @FXML
    public JFXButton btn_MainDescription;
    @FXML
    public JFXButton btn_SecondDescription;
    @FXML
    public VBox box_MainDescription;
    @FXML
    public Text title_main;
    @FXML
    public Text info_main;
    @FXML
    public Text description_main;
    @FXML
    public JFXButton btn_trailer;
    @FXML
    public HBox box_SecondDescription;
    @FXML
    public Text rating_second;
    @FXML
    public Text number_ratings_second;
    @FXML
    public Text full_description_second;
    @FXML
    public JFXListView<String> roleListView_second;
    @FXML
    public Text originalName_second;
    @FXML
    public JFXListView<String> editorsListView_second;
    @FXML
    public WebView trailer;
    @FXML
    public JFXButton closeWeb;

    @FXML
    void initialize() {
        allinfo.setVisible(false);

        ComboBox.valueProperty().addListener((observableValue, s, t1) -> {
            find_item = t1;
            try {
                BoxBlur boxBlur = new BoxBlur(0, 0, 0);
                imageViewBackground.setEffect(boxBlur);
                box_SecondDescription.setVisible(false);
                box_MainDescription.setVisible(true);
                showFilm();

            } catch (ParseException | IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

    }

    public void actionEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            find_item = ComboBox.getEditor().getText();
            JSONObject jo = null;
            try {
                jo = Parser.ParseByKeyWord(find_item);
            } catch (IOException | URISyntaxException | ParseException e) {
                e.printStackTrace();
            }
            assert jo != null;
            ComboBox.getItems().clear();
            ComboBox.getEditor().setText(find_item);
            for (Object film : (JSONArray) jo.get("films")) {
                ComboBox.getItems().addAll((String) ((JSONObject) film).get("nameRu"));
            }
        }
    }

    public void showFilm() throws ParseException, IOException, URISyntaxException {
        var aaa = Parser.ParseByKeyWord(find_item).get("films").toString();
        if (aaa.equals("[]")) {
            System.out.println("Not found film");
            return;
        }
        allinfo.setVisible(true);
        JSONObject Data = (JSONObject) ((JSONArray) Parser.ParseByKeyWord(find_item).get("films")).get(0);
        JSONObject film = Parser.ParseById(((Long) Data.get("filmId")).toString());
        JSONObject film_dop = Parser.ParseByOutherId(Data.get("filmId").toString(), film.get("type").toString());
        String trailerUrl = (String) film_dop.get("trailer");

        title_main.setText(film.get("nameRu").toString());
        originalName_second.setText(film.get("nameEn").toString());

        StringBuilder countries = new StringBuilder();
        for (Object country : (JSONArray) film.get("countries")) {
            countries.append((String) ((JSONObject) country).get("country"));
            countries.append(", ");
        }

        StringBuilder genres = new StringBuilder();
        for (Object genre : (JSONArray) film.get("genres")) {
            genres.append((String) ((JSONObject) genre).get("genre"));
            genres.append(", ");

        }

        for (Object director : (JSONArray) film_dop.get("directors")) {
            editorsListView_second.getItems().addAll((String) director);
        }
        for (Object actor : (JSONArray) film_dop.get("actors")) {
            roleListView_second.getItems().add((String) actor);
        }


        String filmlength = Data.get("filmLength").toString();
        String[] times = filmlength.split(":");
        if (times[0].equals("1")) filmlength = times[0] + " час ";
        else filmlength = times[0] + "часа";
        filmlength += times[1] + " мин";

        String info_main_text = Data.get("rating") + ", " + film.get("year") + ", " + genres + " " + countries + " " + filmlength;

        info_main.setText(info_main_text);
        rating_second.setText((String) Data.get("rating"));
        Long countRate = (Long) Data.get("ratingVoteCount");
        number_ratings_second.setText(String.valueOf(countRate));
        full_description_second.setText((String) film_dop.get("description"));
        description_main.setText((String) Data.get("description"));

        URLConnection connection = new URL((String) Data.get("posterUrl")).openConnection();
        connection.connect();
        imageViewBackground.setImage(new Image(connection.getInputStream()));

        btn_MainDescription.setOnAction(actionEvent -> {
            BoxBlur boxBlur = new BoxBlur(0, 0, 0);
            imageViewBackground.setEffect(boxBlur);
            box_SecondDescription.setVisible(false);
            box_MainDescription.setVisible(true);
        });
        btn_SecondDescription.setOnAction(actionEvent -> {
            BoxBlur boxBlur = new BoxBlur(5, 5, 3);
            imageViewBackground.setEffect(boxBlur);
            box_SecondDescription.setVisible(true);
            box_MainDescription.setVisible(false);
        });
        btn_trailer.setOnAction(actionEvent -> {

            if (trailerUrl != null) {
                trailer.setVisible(true);
                closeWeb.setVisible(true);
                trailer.getEngine().load(trailerUrl);
                trailer.getEngine().executeScript(getJSAudioVideo(false));
            }

        });
        closeWeb.setOnAction(actionEvent -> {
            closeWeb.setVisible(false);
            trailer.setVisible(false);
            trailer.getEngine().executeScript(getJSAudioVideo(true));
        });
    }

    public String getJSAudioVideo(boolean mute) {
        return "var videos = document.querySelectorAll('video'),\n" +
                "    audios = document.querySelectorAll('listen');\n" +
                "    [].forEach.call(videos, function(video) { video.muted = " + mute + "; });\n" +
                "    [].forEach.call(audios, function(audio) { audio.muted = " + mute + "; });";
    }
}

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import javax.imageio.ImageIO;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Uwais M
 *
 */
public class ClientPane extends GridPane {

	//setup streams
	private Socket CSocket = null;
	private InputStream is = null;
	private OutputStream os = null;
	private BufferedReader br = null;
	private DataOutputStream dos = null;
	
	//GUI Elements
	//Preprocessor
	private Button btnGrayscale;
	
	//Connect
	private Button btnConnect;
	
	//load Image
	private Button btnLoadImage;
	
	//Feature Extraction
	private Button btnCan;
	private ImageView imageview;
	
	//for api
	private String grayURL = "/api/GrayScale";
	private String CanURL = "/api/Canny";
	
	public ClientPane(Stage mStage) {
		
		setupGUI();
		
		//Button creation for all commands
		//connect
    	btnConnect.setOnAction(e->{
    		Connect();
    	});
    	
		//load image
    	btnLoadImage.setOnAction(e->{
    		FileChooser FC = new FileChooser();
    		FC.setInitialDirectory(new File("./data"));
    		FC.setTitle("Choose a .jpg file");
    		Stage nstage = new Stage();
    		File File = FC.showOpenDialog(nstage);
    		
    		if(File!= null)
    		{
    			try {
    				FileInputStream fin = new FileInputStream(File);

    				byte[] arrBytes = new byte[(int) File.length()];
    				fin.read(arrBytes);

                    String base = new String(Base64.getEncoder().encode(arrBytes));
    				System.out.println(base);

    				Image sImage = new Image(new ByteArrayInputStream(arrBytes));
    				imageview.setImage(sImage);
    				imageview.resize(400,400);
    				
    				//creates a duplicated image
    				BufferedImage img = ImageIO.read(new ByteArrayInputStream(arrBytes));
    				File edFile = new File("data/edited.jpg");
    				ImageIO.write(img, "jpg", edFile);

    				File = edFile;
    				fin.close();
    				
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
    		}
    		
    	});
    	
      	//grayscale black and white
    	btnGrayscale.setOnAction(e->{
    		Connect();
    		useAPI(grayURL);
    	});
    	
    	//Canny
    	btnCan.setOnAction(e->{
    		Connect();
    		useAPI(CanURL);
    	});
	}
	
    private void setupGUI() {
    	setHgap(10);
    	setVgap(10);
    	setAlignment(Pos.CENTER);
    	
    	btnConnect = new Button("Connect");
    	
    	btnLoadImage = new Button("Upload");
    	
    	btnGrayscale = new Button("Grayscale");
    	
    	btnCan = new Button("Canny");
    	
    	imageview = new ImageView();
    	imageview.setFitHeight(400);
    	imageview.setFitWidth(400);
    	imageview.resize(400,400);
    	
    	add(btnConnect,0,0);
    	add(btnLoadImage,5,0);
    	add(imageview,1,3);
    	add(btnGrayscale,0,4);
    	add(btnCan,1,4);		
    }
    
   
    //connect to the server
 private void Connect()
 {
		try {
			CSocket = new Socket("localhost", 5000);
			os = CSocket.getOutputStream();
			is = CSocket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is)); 
			dos = new DataOutputStream(os);
		System.out.println("Connected to server"); 
		}catch (UnknownHostException e1){
		e1.printStackTrace();
		}catch (IOException ie) {
		ie.printStackTrace();	
		} 
 }
 
 //calling api
 private void useAPI(String url)
 {
	 String encFile = null;
		try {
			File imgFile = new File("data/edited.jpg");

			if (imgFile.exists()) {

			 FileInputStream fin = new FileInputStream(imgFile);
				byte[] arrBytes = new byte[(int) imgFile.length()];
				fin.read(arrBytes);

				encFile = new String(Base64.getEncoder().encodeToString(arrBytes));
				byte[] sendBytes = encFile.getBytes();

				//sending image to http protocol
				dos.write(("POST " + url + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " + "application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encFile.length() + "\r\n").getBytes());
				dos.write(("\r\n").getBytes());
				dos.write(sendBytes);	
				dos.flush();
				dos.write(("\r\n").getBytes());
				
				String resp = "";
				String line = "";

				while (!(line = br.readLine()).equals("")) {
					resp += line + "\n";
				}
				System.out.println(resp);

				String imgData = "";
				while ((line = br.readLine()) != null) {
					imgData += line;
				}
				System.out.println(imgData);

				String base = imgData.substring(imgData.indexOf('\'') + 1, imgData.lastIndexOf('}') - 1);
				System.out.println(base);

				//decode the string 
				byte[] decStr = Base64.getDecoder().decode(base);

				Image gray = new Image(new ByteArrayInputStream(decStr));
				imageview.setImage(gray);

			   //creating a new img
				BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(decStr));
				ImageIO.write(bImage, "jpg", imgFile);
				
				fin.close();

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
 }
 


}

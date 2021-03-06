/*
* XML Parsing using kxml2
* Author : Naveen Balani
*/

//KXML Apis
import org.kxml2.io.*;
import org.xmlpull.v1.*;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;

import java.io.*;
import java.util.Vector;

public class XMLJ2MEService extends MIDlet implements CommandListener, ItemStateListener {

    //Form Name
    Form mainForm = new Form ("SampleJ2MEXML");
    Form detailsForm = new Form ("WordDetails");
    TextField txtField = new TextField( "search:", "", 50, TextField.ANY);
    StringItem wordDetails = new StringItem("","");
    //Location of xml file
    Vector wordVector = new Vector();
    ChoiceGroup choiceGroup = new ChoiceGroup("Results",Choice.EXCLUSIVE, new String[] {}, null);
    Font bigFont = Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_LARGE);
    private final static Command xmlCommand = new Command("Get XML Data", Command.OK, 1);     
    private final static Command clearCommand = new Command("Clear", Command.BACK, 2);     
    private final static Command back = new Command("Back", Command.BACK, 3);     
    class ReadXML extends Thread {
      
    StringBuffer sb = new  StringBuffer(); 	
    String url = "http://turlewicz.com:4567/edict/^sora.xml";
      public void displayResult() {
     	    //Display parsed  XML file
     	    for(int i= 0 ; i< wordVector.size() ;i++){
     	    	Word word = (Word) wordVector.elementAt(i);
     	    	choiceGroup.append(word.getName(),null);
     	      }	
       }
      public void run() {
        try {
          //Open http connection
          HttpConnection httpConnection = (HttpConnection) Connector.open(url);
          wordVector.removeAllElements();  //clear vector with words 
          //Initilialize XML parser
          KXmlParser parser = new KXmlParser();
          parser.setInput(new InputStreamReader(httpConnection.openInputStream(),"UTF-8"));
          parser.nextTag();
          parser.require(XmlPullParser.START_TAG, null, "catalog");
          //Iterate through our XML file
          while (parser.nextTag () != XmlPullParser.END_TAG)
                   readXMLData(parser);
          parser.require(XmlPullParser.END_TAG, null, "catalog");
          parser.next();
          parser.require(XmlPullParser.END_DOCUMENT, null, null);
          displayResult();
        }
         catch (Exception e) {
                e.printStackTrace ();
        }
      }
      public void setUrl(String u){
        url = u;
      }
    }
    
    public XMLJ2MEService () {
        wordDetails.setFont(bigFont);
        mainForm.append (txtField);
        mainForm.append (choiceGroup);
    	  mainForm.addCommand (xmlCommand);
        mainForm.addCommand (clearCommand);
	      mainForm.setCommandListener (this);
        mainForm.setItemStateListener (this);
        detailsForm.append (wordDetails);
        detailsForm.addCommand (back);
        detailsForm.setCommandListener (this);
     }
      
    public void startApp () {
	    Display.getDisplay (this).setCurrent (mainForm);
    }

    public void pauseApp () {
    }

    public void destroyApp (boolean unconditional) {
    }  


   public void itemStateChanged (Item item) {
      if (item == choiceGroup) {
        int index = choiceGroup.getSelectedIndex ();
        Word w = (Word) wordVector.elementAt(index);
        wordDetails.setLabel(w.getName()+'\n');
        wordDetails.setText(w.getDescription());
        Display.getDisplay (this).setCurrent (detailsForm);
      }
   }

   public void commandAction(Command c, Displayable d) {
     if (c == xmlCommand) {
	        ReadXML readXML = new ReadXML();
          readXML.setUrl("http://turlewicz.com:4567/edict/"+txtField.getString()+".xml");
          readXML.start();
    }
    if (c == clearCommand) {
       //mainForm.deleteAll();
       //mainForm.append(txtField); 
       choiceGroup.deleteAll();
	  }
    if (c == back) {
      Display.getDisplay (this).setCurrent (mainForm);
    }
   }
    private void readXMLData(KXmlParser parser)
			throws IOException, XmlPullParserException {

			//Parse our XML file
			parser.require(XmlPullParser.START_TAG, null, "title");
			Word word = new Word();

			while (parser.nextTag() != XmlPullParser.END_TAG) {
				parser.require(XmlPullParser.START_TAG, null, null);
				String name = parser.getName();
				String text = parser.nextText();
				if (name.equals("name"))
					word.setName(text);
				else if (name.equals("description"))
					word.setDescription(text);
											
			parser.require(XmlPullParser.END_TAG, null, name);
			}
			
			wordVector.addElement(word);
			
			parser.require(XmlPullParser.END_TAG, null, "title");
		}
	}

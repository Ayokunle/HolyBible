package holybible;


import holybible.Bible;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class HolyBible {
    
    public static void main(String args[]){
        Bible b = new Bible();
        b.start();
    }
    static PrintWriter writer = null;
    
    public String[] getBooks(){
        String [] books = new String[1];
        books[0] = "1";
        try {
            File fXmlFile = new File("kjv.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            
            doc.getDocumentElement().normalize();
            NodeList list_books = doc.getElementsByTagName("BIBLEBOOK");
            books = new String[list_books.getLength()];
            for (int temp = 0; temp < list_books.getLength(); temp++){
                Node nNode = list_books.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String book = eElement.getAttribute("bname");
                    books[temp] = book;
                }
            }
        }catch(Exception e){
            System.out.println("Error reading file");
        }
        return books;
    }
    
    public int getChapters(String book){
        BufferedReader br;
        int largest_ch = 0;
        try {
            br = new BufferedReader(new FileReader("BibleTable.txt"));
            String str;
            
            while((str = br.readLine()) != null){
                String [] values = str.split("\\t");
                int ch = Integer.parseInt(values[1]);
                if(values[0].equalsIgnoreCase(book) && ch > largest_ch){
                    largest_ch = ch;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HolyBible.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HolyBible.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return largest_ch;
    }
    
    public int getVerse(String book, String chapter){
        BufferedReader br;
        int verse =0;
        try {
            br = new BufferedReader(new FileReader("BibleTable.txt"));
            String str;
            
            while((str = br.readLine()) != null){
                String [] values = str.split("\\t");
                if(values[0].equalsIgnoreCase(book) && values[1].equalsIgnoreCase(chapter)){
                    verse = Integer.parseInt(values[2]);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HolyBible.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HolyBible.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return verse;
    }
    
    public String getPassage(String book, String chapter, String verse){
        String passage ="";
        int num =0;
        try{
            File fXmlFile = new File("kjv.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            
            doc.getDocumentElement().normalize();
            NodeList list_books = doc.getElementsByTagName("BIBLEBOOK");
            
            try {
                writer = new PrintWriter(new BufferedWriter(new FileWriter("BibleTable.txt", true)));
            } catch (IOException ex) {
                Logger.getLogger(HolyBible.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            for (int temp = 0; temp < list_books.getLength(); temp++){
                Node nNode = list_books.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String book_ = eElement.getAttribute("bname");
                    //System.out.println("Book: "+book);
                    
                    if(book_.equalsIgnoreCase(book)){
                        
                        NodeList list_chapters = nNode.getChildNodes();
                        String chapter_;
                        for(int t = 0; t < list_chapters.getLength(); t++){
                            Node chapter_n = list_chapters.item(t);
                            if(chapter_n.getNodeType() == Node.ELEMENT_NODE) {
                                Element chapter_ele = (Element) chapter_n;
                                chapter_ = chapter_ele.getAttribute("cnumber");
                                //System.out.println("Chapter: "+chapter);                   
                                
                                if( chapter_.equalsIgnoreCase(chapter)){
                                   
                                   NodeList list_verse = chapter_n.getChildNodes();
                                   String verse_ = "";
                                   for(int x = 0; x < list_verse.getLength(); x++){
                                       Node verse_n = list_verse.item(x);
                                       if(verse_n.getNodeType() == Node.ELEMENT_NODE) {
                                        Element verse_ele = (Element) verse_n;
                                        verse_ = verse_ele.getAttribute("vnumber");
                                        //System.out.println("Verse: "+verse);
                                        if(verse_.equalsIgnoreCase(verse)){
                                            String a = verse_ele.getTextContent();
                                            
                                            int verse_int = Integer.parseInt(verse);
                                            if(num != 0){
                                                passage = passage +"\n"+verse+". " + a;
                                            }else{
                                                passage = passage +verse+". " + a;    
                                            }
                                            num = Integer.parseInt(verse);
                                            num++;
                                            verse = Integer.toString(num);
                                           }   
                                       }   
                                   }
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.append("Reading file error.");
        }
        return passage;
    }
}

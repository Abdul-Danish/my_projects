package com.design.pattern.behavioural.memento;

import java.util.ArrayList;
import java.util.List;

/*
 * Without violating encapsulation, the internal state of an object can be captured and restored 
 * using the Memento design pattern. It gives you the power to undo or roll back changes you’ve 
 * made to an object by saving and restoring its state to a prior one. You can save 
 * checkpoints in your program as it develops so you can return to them at a later time.
 * 
 * Similar To: -
 */
public class MementoDesignPattern {

    public static void main(String[] args) {
        History history = new History();
        Document doc = new Document("Document Created");
        doc.write("\nUpdated Document");
        System.out.println(doc.getContent()); // 1
        System.out.println();
        
        DocumentMemento save1 = doc.save();
        history.addMomento(save1);
        doc.clear();
        
        doc.write("writing after save1");
        DocumentMemento save2 = doc.save();
        history.addMomento(save2);
        
        System.out.println(doc.getContent()); // 2
        System.out.println();
        System.out.println(save1.getSavedContent()); // 3
        System.out.println();
        System.out.println(save2.getSavedContent()); // 4
        
        System.out.println("======================================");
        for (int i=0; i<history.getHistoryLength(); i++) {
            System.out.println(history.getMemento(i).getSavedContent());
            System.out.println();
        }
    }
}


// Originator
class Document {
    private String content = "";
    
    public Document(String content) {
        this.content = content;
    }
    
    public void write(String text) {
//        this.content += (text.startsWith(" ") && !text.startsWith("\n")) ? text : " " + text;
        this.content += text;
    }
    
    public DocumentMemento save() {
        return new DocumentMemento(this.content);
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void clear() {
        this.content = "";
    }
}

// Memento
class DocumentMemento {
    private String content = "";
    
    public DocumentMemento(String content) {
        this.content += content;
    }
    
    public String getSavedContent() {
        return this.content;
    }
}

// CareTaker
class History {
    private List<DocumentMemento> mementos = new ArrayList<>();
    
    public void addMomento(DocumentMemento documentMemento) {
        this.mementos.add(documentMemento);
    }
    
    public DocumentMemento getMemento(int index) {
        try {
            return mementos.get(index);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid Version Provided");
            return null;
        }
    }
    
    public int getHistoryLength() {
        return mementos.size();
    }
}

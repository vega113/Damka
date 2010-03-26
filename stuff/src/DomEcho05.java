/*
 * @(#)DomEcho05.java	1.9 98/11/10
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

// Basic GUI components
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

// GUI components for right-hand side
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;

// GUI support classes
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

// For creating borders
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

// For creating a TreeModel
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;

public class DomEcho05  extends JPanel
{
    // Global value so it can be ref'd by the tree-adapter
    static Document document; 

    boolean compress = false;
    static final int windowHeight = 460;
    static final int leftWidth = 300;
    static final int rightWidth = 340;
    static final int windowWidth = leftWidth + rightWidth;

    public DomEcho05()
    {
       // Make a nice border
       EmptyBorder eb = new EmptyBorder(5,5,5,5);
       BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
       CompoundBorder cb = new CompoundBorder(eb,bb);
       this.setBorder(new CompoundBorder(cb,eb));

       // Set up the tree
       JTree tree = new JTree(new DomToTreeModelAdapter());

       // Iterate over the tree and make nodes visible
       // (Otherwise, the tree shows up fully collapsed)
       //TreePath nodePath = ???;
       //  tree.expandPath(nodePath); 

       // Build left-side view
       JScrollPane treeView = new JScrollPane(tree);
       treeView.setPreferredSize(  
           new Dimension( leftWidth, windowHeight ));

       // Build right-side view
       // (must be final to be referenced in inner class)
       final 
       JEditorPane htmlPane = new JEditorPane("text/html","");
       htmlPane.setEditable(false);
       JScrollPane htmlView = new JScrollPane(htmlPane);
       htmlView.setPreferredSize( 
           new Dimension( rightWidth, windowHeight ));

       // Wire the two views together. Use a selection listener 
       // created with an anonymous inner-class adapter.
       tree.addTreeSelectionListener(
         new TreeSelectionListener() {
           public void valueChanged(TreeSelectionEvent e) {
             TreePath p = e.getNewLeadSelectionPath();
             if (p != null) {
               AdapterNode adpNode = 
                  (AdapterNode) p.getLastPathComponent();
               htmlPane.setText(adpNode.content());
             }
           }
         }
       );

       // Build split-pane view
       JSplitPane splitPane = 
          new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
                          treeView,
                          htmlView );
       splitPane.setContinuousLayout( true );
       splitPane.setDividerLocation( leftWidth );
       splitPane.setPreferredSize( 
            new Dimension( windowWidth + 10, windowHeight+10 ));

       // Add GUI components
       this.setLayout(new BorderLayout());
       this.add("Center", splitPane );
    } // constructor

    public static void main(String argv[])
    {
        if (argv.length < 1) {
        	String path = "";
        	for (int i = 0; i < argv.length; i++) {
				path += argv[i];
				argv[i]=null;
			}
        	argv[0]=path;
	        buildDom();
	        makeFrame();
	        return;
	        }

        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);   
        //factory.setNamespaceAware(true);
        try {
           DocumentBuilder builder = factory.newDocumentBuilder();
           document = builder.parse( new File(argv[0]) );
            makeFrame();
 
        } catch (SAXException sxe) {
           // Error generated during parsing)
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        } catch (IOException ioe) {
           // I/O error
           ioe.printStackTrace();
        }
    } // main

    public static void makeFrame() {
        // Set up a GUI framework
        JFrame frame = new JFrame("DOM Echo");
        frame.addWindowListener(
          new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
          }  
        );

        // Set up the tree, the views, and display it all
        final DomEcho05 echoPanel = 
           new DomEcho05();
        frame.getContentPane().add("Center", echoPanel );
        frame.pack();
        Dimension screenSize = 
           Toolkit.getDefaultToolkit().getScreenSize();
        int w = windowWidth + 10;
        int h = windowHeight + 10;
        frame.setLocation(screenSize.width/3 - w/2, 
                          screenSize.height/2 - h/2);
        frame.setSize(w, h);
        frame.setVisible(true);
    } // makeFrame

    public static void buildDom()
    {
        DocumentBuilderFactory factory =
           DocumentBuilderFactory.newInstance();
        try {
          DocumentBuilder builder = factory.newDocumentBuilder();
          document = builder.newDocument();  // Create from whole cloth

          Element root = 
                  (Element) document.createElement("rootElement"); 
          document.appendChild(root);
          root.appendChild( document.createTextNode("Some") );
          root.appendChild( document.createTextNode(" ")    );
          root.appendChild( document.createTextNode("text") );


        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();

        }
    } // buildDom

    // An array of names for DOM node-types
    // (Array indexes = nodeType() values.)
    static final String[] typeName = {
        "none",
        "Element",
        "Attr",
        "Text",
        "CDATA",
        "EntityRef",
        "Entity",
        "ProcInstr",
        "Comment",
        "Document",
        "DocType",
        "DocFragment",
        "Notation",
    };
    static final int ELEMENT_TYPE =   1;
    static final int ATTR_TYPE =      2;
    static final int TEXT_TYPE =      3;
    static final int CDATA_TYPE =     4;
    static final int ENTITYREF_TYPE = 5;
    static final int ENTITY_TYPE =    6;
    static final int PROCINSTR_TYPE = 7;
    static final int COMMENT_TYPE =   8;
    static final int DOCUMENT_TYPE =  9;
    static final int DOCTYPE_TYPE =  10;
    static final int DOCFRAG_TYPE =  11;
    static final int NOTATION_TYPE = 12;
    // The list of elements to display in the tree
    // Could set this with a command-line argument, but
    // not much point -- the list of tree elements still
    // has to be defined internally.
    // Extra credit: Read the list from a file
    // Super-extra credit: Process a DTD and build the list.
   static String[] treeElementNames = {
        "slideshow",
        "slide",
        "title",         // For slideshow #1
        "slide-title",   // For slideshow #10
        "item",
    };
    boolean treeElement(String elementName) {
      for (int i=0; i<treeElementNames.length; i++) {
        if ( elementName.equals(treeElementNames[i]) ) return true;
      }
      return false;
    }

    // This class wraps a DOM node and returns the text we want to
    // display in the tree. It also returns children, index values,
    // and child counts.
    public class AdapterNode 
    { 
      org.w3c.dom.Node domNode;

      // Construct an Adapter node from a DOM node
      public AdapterNode(org.w3c.dom.Node node) {
        domNode = node;
      }

      // Return a string that identifies this node in the tree
      // *** Refer to table at top of org.w3c.dom.Node ***
      public String toString() {
        String s = typeName[domNode.getNodeType()];
        String nodeName = domNode.getNodeName();
        if (! nodeName.startsWith("#")) {
           s += ": " + nodeName;
        }
        if (compress) {
           String t = content().trim();
           int x = t.indexOf("\n");
           if (x >= 0) t = t.substring(0, x);
           s += " " + t;
           return s;
        }
        if (domNode.getNodeValue() != null) {
           if (s.startsWith("ProcInstr")) 
              s += ", "; 
           else 
              s += ": ";
           // Trim the value to get rid of NL's at the front
           String t = domNode.getNodeValue().trim();
           int x = t.indexOf("\n");
           if (x >= 0) t = t.substring(0, x);
           s += t;
        }
        return s;
      }

      public String content() {
        String s = "";
        org.w3c.dom.NodeList nodeList = domNode.getChildNodes();
        for (int i=0; i<nodeList.getLength(); i++) {
          org.w3c.dom.Node node = nodeList.item(i);
          int type = node.getNodeType();
          AdapterNode adpNode = new AdapterNode(node); //inefficient, but works
          if (type == ELEMENT_TYPE) {
            // Skip subelements that are displayed in the tree.   
            if ( treeElement(node.getNodeName()) ) continue;

            // EXTRA-CREDIT HOMEWORK:
            //   Special case the SLIDE element to use the TITLE text
            //   and ignore TITLE element when constructing the tree.

            // EXTRA-CREDIT
            //   Convert ITEM elements to html lists using
            //   <ul>, <li>, </ul> tags

            s += "<" + node.getNodeName() + ">";
            s += adpNode.content();
            s += "</" + node.getNodeName() + ">";
          } else if (type == TEXT_TYPE) {
            s += node.getNodeValue();
          } else if (type == ENTITYREF_TYPE) {
            // The content is in the TEXT node under it
            s += adpNode.content();
          } else if (type == CDATA_TYPE) {
            // The "value" has the text, same as a text node.
            //   while EntityRef has it in a text node underneath.
            //   (because EntityRef can contain multiple subelements)
            // Convert angle brackets and ampersands for display
            StringBuffer sb = new StringBuffer( node.getNodeValue() );
            for (int j=0; j<sb.length(); j++) {
              if (sb.charAt(j) == '<') {
                sb.setCharAt(j, '&');
                sb.insert(j+1, "lt;");
                j += 3;
              } else if (sb.charAt(j) == '&') {
                sb.setCharAt(j, '&');
                sb.insert(j+1, "amp;");
                j += 4;
              }
            }
            s += "<pre>" + sb + "\n</pre>";
          }
           // Ignoring these:
           //   ATTR_TYPE      -- not in the DOM tree
           //   ENTITY_TYPE    -- does not appear in the DOM
           //   PROCINSTR_TYPE -- not "data"
           //   COMMENT_TYPE   -- not "data"
           //   DOCUMENT_TYPE  -- Root node only. No data to display.
           //   DOCTYPE_TYPE   -- Appears under the root only
           //   DOCFRAG_TYPE   -- equiv. to "document" for fragments
           //   NOTATION_TYPE  -- nothing but binary data in here
        }
        return s;
      }

      /*
       * Return children, index, and count values
       */
      public int index(AdapterNode child) {
        //System.err.println("Looking for index of " + child);
        int count = childCount();
        for (int i=0; i<count; i++) {
          AdapterNode n = this.child(i);
          if (child.domNode == n.domNode) return i;
        }
        return -1; // Should never get here.
      }

      public AdapterNode child(int searchIndex) {
        //Note: JTree index is zero-based. 
        org.w3c.dom.Node node = 
             domNode.getChildNodes().item(searchIndex);
        if (compress) {
          // Return Nth displayable node
          int elementNodeIndex = 0;
          for (int i=0; i<domNode.getChildNodes().getLength(); i++) {
            node = domNode.getChildNodes().item(i);
            if (node.getNodeType() == ELEMENT_TYPE 
            && treeElement( node.getNodeName() )
            && elementNodeIndex++ == searchIndex) {
               break; 
            }
          }
        }
        return new AdapterNode(node); 
      }

      public int childCount() {
        if (!compress) {
          // Indent this
          return domNode.getChildNodes().getLength();  
        } 
        int count = 0;
        for (int i=0; i<domNode.getChildNodes().getLength(); i++) {
           org.w3c.dom.Node node = domNode.getChildNodes().item(i); 
           if (node.getNodeType() == ELEMENT_TYPE
           && treeElement( node.getNodeName() )) 
           {
             // Note: 
             //   Have to check for proper type. 
             //   The DOCTYPE element also has the right name
             ++count;
           }
        }
        return count;
      }
    }

    // This adapter converts the current Document (a DOM) into 
    // a JTree model. 
    public class DomToTreeModelAdapter 
      implements javax.swing.tree.TreeModel 
    {
      // Basic TreeModel operations
      public Object  getRoot() {
        //System.err.println("Returning root: " +document);
        return new AdapterNode(document);
      }
      public boolean isLeaf(Object aNode) {
        // Determines whether the icon shows up to the left.
        // Return true for any node with no children
        AdapterNode node = (AdapterNode) aNode;
        if (node.childCount() > 0) return false;
        return true;
      }
      public int     getChildCount(Object parent) {
        AdapterNode node = (AdapterNode) parent;
        return node.childCount();
      }
      public Object getChild(Object parent, int index) {
        AdapterNode node = (AdapterNode) parent;
        return node.child(index);
      }
      public int getIndexOfChild(Object parent, Object child) {
        AdapterNode node = (AdapterNode) parent;
        return node.index((AdapterNode) child);
      }
      public void valueForPathChanged(TreePath path, Object newValue) {
        // Null. We won't be making changes in the GUI
        // If we did, we would ensure the new value was really new,
        // adjust the model, and then fire a TreeNodesChanged event.
      }

      /*
       * Use these methods to add and remove event listeners.
       * (Needed to satisfy TreeModel interface, but not used.)
       */
      private Vector listenerList = new Vector();
      public void addTreeModelListener(TreeModelListener listener) {
        if ( listener != null 
        && ! listenerList.contains( listener ) ) {
           listenerList.addElement( listener );
        }
      }
      public void removeTreeModelListener(TreeModelListener listener) {
        if ( listener != null ) {
           listenerList.removeElement( listener );
        }
      }

      // Note: Since XML works with 1.1, this example uses Vector.
      // If coding for 1.2 or later, though, I'd use this instead:
      //   private List listenerList = new LinkedList();
      // The operations on the List are then add(), remove() and
      // iteration, via:
      //  Iterator it = listenerList.iterator();
      //  while ( it.hasNext() ) {
      //    TreeModelListener listener = (TreeModelListener) it.next();
      //    ...
      //  }

      /*
       * Invoke these methods to inform listeners of changes.
       * (Not needed for this example.)
       * Methods taken from TreeModelSupport class described at 
       *   http://java.sun.com/products/jfc/tsc/articles/jtree/index.html
       * That architecture (produced by Tom Santos and Steve Wilson)
       * is more elegant. I just hacked 'em in here so they are
       * immediately at hand.
       */
      public void fireTreeNodesChanged( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
          TreeModelListener listener = 
            (TreeModelListener) listeners.nextElement();
          listener.treeNodesChanged( e );
        }
      } 
      public void fireTreeNodesInserted( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
           TreeModelListener listener =
             (TreeModelListener) listeners.nextElement();
           listener.treeNodesInserted( e );
        }
      }   
      public void fireTreeNodesRemoved( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
          TreeModelListener listener = 
            (TreeModelListener) listeners.nextElement();
          listener.treeNodesRemoved( e );
        }
      }   
      public void fireTreeStructureChanged( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
          TreeModelListener listener =
            (TreeModelListener) listeners.nextElement();
          listener.treeStructureChanged( e );
        }
      }
    }
}

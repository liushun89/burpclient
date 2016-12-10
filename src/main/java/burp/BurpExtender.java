package burp;

import com.shun.liu.shunzhitianxia.bean.ClientRequest;
import com.shun.liu.shunzhitianxia.client.object.ObjectEchoClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * burp客户端
 * @author liushun
 */
public class BurpExtender
        implements IBurpExtender, IContextMenuFactory{
    private OutputStream outputStream;
    private IExtensionHelpers helpers;
    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks){

        callbacks.setExtensionName("send2server");
        callbacks.registerContextMenuFactory(this);
        helpers =callbacks.getHelpers();
        outputStream =callbacks.getStdout();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(outputStream));
        Thread.currentThread().setContextClassLoader(BurpExtender.this.getClass().getClassLoader());
        output("i'm here");
    }

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "alert", "alert", JOptionPane.NO_OPTION);



    }
    @Override
    public List<JMenuItem> createMenuItems(final IContextMenuInvocation invocation) {

        IHttpRequestResponse[] messages = invocation.getSelectedMessages();
        if(messages != null && messages.length > 0) {
            ArrayList list = new ArrayList();
            final IHttpService service = messages[0].getHttpService();
            final byte[] selectedRequest = messages[0].getRequest();
            JMenuItem menuItem = new JMenuItem("Send to Super Server");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        IExtensionHelpers iExtensionHelpers = BurpExtender.this.helpers;
                        IRequestInfo request = iExtensionHelpers.analyzeRequest(service, selectedRequest);
                        ClientRequest clientRequest = new ClientRequest();
                        clientRequest.setRequestURL(request.getUrl().toString());
                        clientRequest.setRequestMsg(selectedRequest);
                        new ObjectEchoClient().connect("127.0.0.1",18800,clientRequest);
//                        Map<String, String> getData = new HashMap<String, String>();
//                        try {
//                            getData.put("url", request.getUrl().toString());
//                            getData.put("body",new String(selectedRequest,"utf-8"));
//                        } catch (UnsupportedEncodingException e1) {
//                            e1.printStackTrace();
//                        }
//                        try {
//                            HttpRequest get = getRequestMethod(getData, url, "post");
//                            httpClient.run(url, get);
//                        } catch (Exception ee) {
//                            ee.printStackTrace();
//                        }
                    } catch (Exception var4) {
                        var4.printStackTrace();
                    }

                }
            });
            list.add(menuItem);
            return list;
        } else {
            return null;
        }
//        List<JMenuItem> listMenuItems = new ArrayList<JMenuItem>();
//        final JMenu jMenu = new JMenu("Send to Super Server");
//        final byte[] request = invocation.getSelectedMessages()[0].getRequest();
//        jMenu.addMouseListener(new MouseListener() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
////                output("mouseClicked");
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
////                output("mousePressed");
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
////                output("mouseReleased");
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
////                output("mouseEntered");
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
////                output("mouseExited");
//            }
//        });
//        listMenuItems.add(jMenu);
//        return listMenuItems;
    }
    private void output(String msg) {
        try {
            outputStream.write(msg.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
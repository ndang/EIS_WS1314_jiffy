import java.awt.EventQueue;

import javax.net.SocketFactory;
import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;

import javax.swing.DefaultListModel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainApplicationConsumerUI {

	private JFrame frame;
	private JLabel lblStatus;
	private JPasswordField textFieldPass;
	private JTextField textFieldUser;
	private JTextField textFieldTopic;
	private JTextArea textArea;
	private JButton btnConnect;
	
	private MqttClient mqttClient;
	private boolean connectionEstablished = false;
	
	
	private DefaultListModel<String> topicsListModel = new DefaultListModel<String>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
            // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApplicationConsumerUI window = new MainApplicationConsumerUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApplicationConsumerUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 600, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				lblStatus.setText("Connecting!");
				
				try {
					mqttClient = new MqttClient("ssl://localhost:8883", "Swing - Consumer", new MemoryPersistence());
					
					MqttConnectOptions mqttOpts = new MqttConnectOptions();
					mqttOpts.setUserName(textFieldUser.getText());
					mqttOpts.setPassword(textFieldPass.getPassword());
					mqttOpts.setCleanSession(false);
					SocketFactory sf = OwnSSLSocketFactory.getSocketFactory(
													new FileInputStream("res/broker.ks"), "koelnerdom",
													new FileInputStream("res/client.ts"), "koelnerdom");
					mqttOpts.setSocketFactory(sf);
					
					mqttClient.connect(mqttOpts);
					
					lblStatus.setText("Connection successful!");
					connectionEstablished = true;
					btnConnect.setEnabled(false);
					
					mqttClient.setCallback(new MqttCallback() {
						
						@Override
						public void messageArrived(String topic, MqttMessage msg) throws Exception {
							String payload = new String(msg.getPayload());
							
							textArea.setText(topic + " | " + payload + "\r\n" + textArea.getText());
						}
						
						@Override
						public void deliveryComplete(IMqttDeliveryToken arg0) {
							
						}
						
						@Override
						public void connectionLost(Throwable arg0) {
							lblStatus.setText("Connection lost!");
						}
					});
					
				} catch (MqttException e) {
					
					lblStatus.setText("Connection failed!");
				} catch  (Exception e) {
					System.out.println(e.getMessage());
					lblStatus.setText("SSLSocketFailure!");
				}
				
			}
		});
		btnConnect.setBounds(151, 110, 150, 30);
		frame.getContentPane().add(btnConnect);
		
		textFieldPass = new JPasswordField();
		textFieldPass.setText("Christa2");
		textFieldPass.setBounds(151, 68, 150, 30);
		frame.getContentPane().add(textFieldPass);
		textFieldPass.setColumns(10);
		
		textFieldUser = new JTextField();
		textFieldUser.setText("Peter2");
		textFieldUser.setBounds(151, 26, 150, 30);
		frame.getContentPane().add(textFieldUser);
		textFieldUser.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(53, 31, 86, 20);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(53, 73, 71, 20);
		frame.getContentPane().add(lblPassword);
		
		JLabel lblTopic = new JLabel("Topic");
		lblTopic.setBounds(53, 175, 71, 20);
		frame.getContentPane().add(lblTopic);
		
		textFieldTopic = new JTextField();
		textFieldTopic.setBounds(53, 203, 140, 30);
		frame.getContentPane().add(textFieldTopic);
		textFieldTopic.setColumns(10);
		
		JButton btnSubscribe = new JButton("Subscribe");
		btnSubscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(!connectionEstablished) {
					lblStatus.setText("Connection must be established before topics can be subscribed!");
					return;
				}
				
				final String topicname = textFieldTopic.getText();
				
				try {
					
					mqttClient.subscribe(topicname);
					
					topicsListModel.addElement(topicname);
					
					lblStatus.setText("Subcribed to \"" + topicname + "\"");
				} catch (MqttException e) {
					lblStatus.setText("Couldn't subscribe to \"" + topicname + "\"");
				}
			}
		});
		btnSubscribe.setBounds(205, 203, 96, 30);
		frame.getContentPane().add(btnSubscribe);
		
		lblStatus = new JLabel("Status");
		lblStatus.setBounds(55, 430, 496, 20);
		frame.getContentPane().add(lblStatus);
		
		JScrollPane scrollPaneOutput = new JScrollPane();
		scrollPaneOutput.setBounds(53, 259, 496, 150);
		frame.getContentPane().add(scrollPaneOutput);
		
		textArea = new JTextArea();
		scrollPaneOutput.setViewportView(textArea);
		
		JScrollPane scrollPaneTopics = new JScrollPane();
		scrollPaneTopics.setBounds(357, 68, 189, 167);
		frame.getContentPane().add(scrollPaneTopics);
		
		JList topicsList = new JList(topicsListModel);
		scrollPaneTopics.setViewportView(topicsList);
		
		JLabel lblSubscribedTopics = new JLabel("Subscribed Topics");
		lblSubscribedTopics.setBounds(357, 31, 180, 20);
		frame.getContentPane().add(lblSubscribedTopics);
	}
}

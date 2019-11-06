import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
//starting class and implements runnable  for using threads
public class BookingProgram extends JFrame implements ActionListener, KeyListener, Runnable {
	
	//define our variables for design our program and using others in the methods
	private JPanel headerPanel, seatsPanel;
	private JTextField seatsNo, agentsNo, waitingTime;
	private JButton createBtn, bookBtn;
	private JTextField textField;
	private ReentrantLock lock = new ReentrantLock();//to let one thread works and the others waiting(one by one)
	private int counter = 0;
	private int agentList[];/*this array gonna get each index as the 
	thread name which start from one, and increase it by one, on each time the agent(thread) booking a new seats
	 .. so we get the number of booked seats for each agent as a value for each index*/
	private static Random ran = new Random();//intializing random for using it in waiting max time 

	public BookingProgram() {//starting constructor

		getContentPane().setLayout(new BorderLayout(0, 0));//border layOut for the Jframe

		//panel for the three text fields and the two buttons as header 
		headerPanel = new JPanel();
		getContentPane().add(headerPanel, BorderLayout.NORTH);

		//intializing our three textFields as seats number, agents number and waiting time for booking between seats
		seatsNo = new JTextField("Number of ThreadClass");
		seatsNo.setHorizontalAlignment(SwingConstants.CENTER);// for let the text in the center
		headerPanel.add(seatsNo);

		agentsNo = new JTextField("Number of Agents");
		agentsNo.setHorizontalAlignment(SwingConstants.CENTER);
		headerPanel.add(agentsNo);

		waitingTime = new JTextField("Maximum Waiting Time");
		waitingTime.setHorizontalAlignment(SwingConstants.CENTER);
		headerPanel.add(waitingTime);

		//intializing the two buttons and add action listener for both of them for running methods to create and booking the seats
		createBtn = new JButton("Create");
		headerPanel.add(createBtn);
		createBtn.addActionListener(this);

		bookBtn = new JButton("Book");
		headerPanel.add(bookBtn);
		bookBtn.addActionListener(this);

		//intializing the center panel which will get the created seats(text fields) 
		seatsPanel = new JPanel();
		getContentPane().add(seatsPanel, BorderLayout.CENTER);
		seatsPanel.setLayout(new GridLayout(10, 0, 5, 9));// will be by gridlay out to be organized by it self as 10 rows and 
		//any no. of colums 

		setSize(850, 900);//size of the window
		setVisible(true);
		setTitle("Booking Program");//title
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		validate();// for solving some problems

	}

	public static void main(String[] args) {

		new BookingProgram();// running our program by calling the constructor
	}

	@Override
	public void run() {// run method which will be worked by "threads.start()" as the method of threads
		// TODO Auto-generated method stub
		bookSeats();// will be used when threads run .. so it will do the seats booking work
	}

	static JTextField[] seatsList;//array list for creating our seats (textFields)

	public void createSeats() {// method for create seats by the given no. on the seats No. text Fields

		int seats = Integer.parseInt(seatsNo.getText().trim());// getting the given value of the seats no. from the textField
		seatsList = new JTextField[seats]; //intializing the seats list by the given number of the seats no text field
			for (int i = 0; i < seats; i++) {// for loop for printing out all of seats list(Textfields list) to the center panel

				JTextField seat = new JTextField("Not Booked");// intialinzing them with this text
				seat.setEditable(false);
				seat.setHorizontalAlignment(JTextField.CENTER);// the text in the center
				seat.setBackground(Color.WHITE);//let the background white
				seatsList[i] = seat;//let the seat = index i in seatlist array

				seatsPanel.add(seatsList[i]);//add the array to the panel 
				validate();

		}
	}

	public void bookSeats() {// for booking the seats
		int waiting = Integer.parseInt(waitingTime.getText().trim());// getting the max waiting time value from its textfield

		// for (int i = 0; i < seatsList.length; i++) {

		while (counter < seatsList.length) {// for passing to each element in the seatslist (each text field) for booking it(update it)
			
			try {// for the sleep exception
				Thread.currentThread().sleep(ran.nextInt(waiting));//let thread wait for a random no. bet 0 to the max waiting time value 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// semaphore.acquire();
			lock.lock();// rentrantlock for let only one thread to go on and let others wait 
			if (counter < seatsList.length) {//if statement to do the updates and printing the last message after finishing updating 
				// seats list elements(text fields)
				seatsList[counter].setBackground(Color.RED);// let its background red
				
				// change its text content and printing the thread name out because each thread have to use this method one time 
				seatsList[counter].setText("Booked by Agent " + Thread.currentThread().getName());
				
				
				agentList[Integer.parseInt(Thread.currentThread().getName())-1]++; /*this array gonna get each index as the 
				thread name which start from one, and increase it by one each time the agent(thread) booking a new seats
				 so we get the number of booked seats for each agent as a value for each index*/
				
				counter++;// to pass to the next indexes in the seats list
			}
			else finish();// for print out the last message after booking all of seats
			lock.unlock();// unlock the rentrantlock so more then thread could continue with each other
			// semaphore.release();

		}

		// }

	}

	private void finish() {// for printing our last message after booking all seats
		// TODO Auto-generated method stub
		String str="";//empty string to get the printed message ready 
		
		for (int i = 0; i < agentList.length; i++) {//for loop to get the no. of booked seats for each thread(agent)
			
			//then we let str = message + thread name + the no. of booked seats for each thread
			str=str+"Agent "+ (i+1) + " Booked "+ agentList[i]+" seats.\n";
		}
		
		JOptionPane.showMessageDialog(this,str);// printing out the message inside the program screen
		System.exit(0);// exit the program after pressing ok
	}

	static Thread[] ThreadList;// array list for the created thread(agents)

	public void agentsGenerator() {// method for generate threads and let them start working by ".start"

		int agents = Integer.parseInt(agentsNo.getText().trim());//getting the no. of created agents(threads) from its textfield
		ThreadList = new Thread[agents];//intializing the array by the no. of created threads
		agentList=new int[agents];// for getting the no of threads then use it for getting the no. of booked seats for each one
		for (int i = 0; i < ThreadList.length; i++) {// for loop to get into each element in the thread array
			ThreadList[i] = new Thread(this, i + 1 + "");//intializing each element in the thread array as an array and gives it
			//a name as 1 to ..
			ThreadList[i].start();// run the thread .. so it go to run method
		}
		// System.out.println("mai");

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(createBtn)) {// if we press on create btn , createSeats method will be run
			createSeats();
		}
		if (e.getSource().equals(bookBtn)) {/*if we press on book btn , 
			agentsGenerator method then -> it will do start for threads -> so run method will run -> which has bookseats method to
			run too*/
			
			agentsGenerator();
		}

	}

}

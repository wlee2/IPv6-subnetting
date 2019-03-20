import java.util.*;
import java.io.*;
import java.net.*;


public class Client {
	static Socket socket;
	static String hostAddress;
	static int socketPort;
	static OutputStream out;
	static InputStream in;
	static PrintWriter pw;
	static BufferedReader br;
	
	static List<String> data;
	
	public static Boolean addressType () {
		SocketAddress socketAddress = socket.getRemoteSocketAddress();

		if (socketAddress instanceof InetSocketAddress) {
			InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
			if (inetAddress instanceof Inet4Address) {
				System.out.println("your connection is IPv4: " + inetAddress);
				return false;
			}
			if (inetAddress instanceof Inet6Address)
				System.out.println("your connection is IPv6: " + inetAddress);
		}
		return true;
	}
	
	public static Boolean isAddress(String address) {
		if (address.isEmpty()) {
			return false;
		}
		try {
			InetAddress check = InetAddress.getByName(address);
			if (!(check instanceof Inet4Address || check instanceof Inet6Address))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static Boolean isIPv6Address(String address) {
		try {
		InetAddress check = InetAddress.getByName(address);
			if (check instanceof Inet6Address)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	public static Boolean isPort(String port) {
		try {
			int check = Integer.parseInt(port);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static void makeConnection() {
		try {
			socket = new Socket(hostAddress, socketPort);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String userInput;
				
			Boolean select = true;			
			while(select) {
				if (addressType()) {
					System.out.println("(1) - It can use your original IP address");
				}
				System.out.println("(2) - It can use your input address.");
				System.out.print("Selection : ");
				userInput = reader.readLine();
				switch(userInput) {
					case "1":
						System.out.print("\t-(Message)you have chosen (1) : ");
						if (addressType()) {
							String ad = socket.getInetAddress().toString();
							SendingProcess(ad.substring(1));
							select = false;
						} 
						else
							System.out.println("\t-You can't use your IPv4 address for it!\n");
						
						break;
					case "2":
						System.out.println("Please enter an IPv6 address (If you enter \"::\" -> defaulted \"2007:db8:affa::\")");
						System.out.print("IPv6 : ");
						userInput = reader.readLine();
						
						if(userInput.equals("::"))
							userInput = "2007:db8:affa::";
						if (isIPv6Address(userInput)) {
							SendingProcess(userInput);
							select = false;
						}
						else
							System.out.println("\t-(Message)Address Error!\n");
						break;
					default :
						System.out.println("\t-(Message)Selection Error!\n");
				}
			}
			
			GetFromServer();
					
			pw.close();
			br.close();
			socket.close();

		} catch (IllegalArgumentException e) {
			System.out.println(e);
			System.out.println("\t-(Message)Arguments have error : IP address may not validate.");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void SendingProcess (String address) {
		while(true) {
			try {
				out = socket.getOutputStream();
				in = socket.getInputStream();
				pw = new PrintWriter(new OutputStreamWriter(out));
				br = new BufferedReader(new InputStreamReader(in));
				Scanner reader = new Scanner(System.in);
				
				int sub = 40;
				int Sites = 1;
				int subSite1 = 0;
				int subSite2 = 0;
				int EUs = 0;
				
				pw.println(address);
				while(true) {
					System.out.print("Enter the subnet mask to create the subnet : ");
					sub = reader.nextInt();
					// if(sub.contains("/"))
						// sub = sub.substring(sub.indexOf('/') + 1);
					if(sub < 0 || sub > 128)
						System.out.print("\t-(Message)The subnet mask must be between 0 and 128.\n");
					else
						break;
				}
				while(true) {
					System.out.print("How many Sites do you need? : ");
					Sites = reader.nextInt();
					if(Sites < 1 || Sites > 16)
						System.out.print("\t-(Message)The Sites must be between 1 and 16.\n");
					else
						break;
				}
				while(true) {
					System.out.print("How many Sub-site 1 do you need? : ");
					subSite1 = reader.nextInt();
					if(subSite1 < 0 || subSite1 > 16)
						System.out.print("\t-(Message)The Sub-site 1 must be between 0 and 16.\n");
					else
						break;
				}
				while(true) {
					System.out.print("How many Sub-site 2 do you need? : ");
					subSite2 = reader.nextInt();
					if(subSite2 < 0 || subSite2 > 16)
						System.out.print("\t-(Message)The Sub-site 2 must be between 0 and 16.\n");
					else
						break;
				}
				while(true) {
					System.out.print("how many EUs do you need? : ");
					EUs = reader.nextInt();
					if(EUs < 0 || EUs > 16)
						System.out.print("\t-(Message)The EU must be between 0 and 16.\n");
					else
						break;
				}

				pw.println(sub);
				pw.println(Sites);
				pw.println(subSite1);
				pw.println(subSite2);
				pw.println(EUs);
				pw.flush();
				
				System.out.println("\t-(Message)Input has been sent to the server!");
				System.out.println("\t-(Message)Depending on the server system, it may take more than a minute.");
				reader.close();
				break;
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	@SuppressWarnings("unchecked")	
	public static void GetFromServer() {
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
			while((data = (List) ois.readObject()) != null) {
				for(int i = 0; i < data.size(); i++) {
					System.out.println(data.get(i));
					writer.write(data.get(i));
					writer.newLine();
				}
				break;
			}
			writer.close();
			ois.close();
			System.out.println("\t-(Message)This data saved in \"results.txt\"");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
    public static void main(String[] args){
		System.setProperty("java.net.preferIPv6Stack","true");
		String defaulted = "2607:f798:804:103:59f8:d096:31ae:30b";
		if (args.length >= 2) {
			if(isAddress(args[0]))
				hostAddress = args[0];
			else {
				hostAddress = defaulted;
				System.out.println("\t-(Message)Argument of IP address has error");
			}
			if (isPort(args[1]))
				socketPort = Integer.parseInt(args[1]);
			else {
				socketPort = 8000;
				System.out.println("\t-(Message)socket Port has error!");
			}
		}
		else if (args.length == 1) {
			if(isAddress(args[0]))
				hostAddress = args[0];
			else {
				hostAddress = defaulted;
				System.out.println("\t-(Message)Argument is not IP address - set defaults");
			}
			
			if (isPort(args[0]))
				socketPort = Integer.parseInt(args[0]);
			else {
				socketPort = 8000;
				System.out.println("\t-(Message)Argument is not socket Port - set defaults");	
			}
		}
		else {
			System.out.println("\t-(Message)Missing arguments - set defaults");
			hostAddress = defaulted;
			socketPort = 8000;
		}
		System.out.println("\t-(Message)IP : [" + hostAddress + "] Port : [" + socketPort + "] start to make a connection");
		makeConnection();	
   }
}




import java.util.*;
import java.io.*;
import java.net.*;

class Data {
	public int Tsites;
	public int TsubSite1;
	public int TsubSite2;
	public int TEUs;
	
	public String startAddress;
	public int startSubnet;
	public Map<String, Integer> sitesMap;
	public Map<String, Integer> subSite1Map;
	public Map<String, Integer> subSite2Map;
	public Map<String, Integer> EUsMap;
			
	ArrayList<String> printerString;	
	ArrayList<String> printerString1;
	ArrayList<String> printerString2;
	ArrayList<String> printerString3;
	ArrayList<String> printerString4;
	
	
	
	Data() {
		startAddress = null;
		printerString = new ArrayList<>();
		sitesMap = new TreeMap<String, Integer>();
		subSite1Map = new TreeMap<String, Integer>();
		subSite2Map = new TreeMap<String, Integer>();
		EUsMap = new TreeMap<String, Integer>();
		
		printerString1 = new ArrayList<>();
		printerString2 = new ArrayList<>();
		printerString3 = new ArrayList<>();
		printerString4 = new ArrayList<>();
	}
	
	String SplitPrint(String a, int b) {
		String str = ":";
		List<String> check = new ArrayList<>();
		int checker = -1;
		for(int k = 0; k < 8; k++) {
			String checkTempString = Integer.toString(Integer.parseInt(a.substring(16 * k, (16 * k)+16), 2), 16);
			check.add(checkTempString);
		}
		int checkZeroIndex = b/16;
		if(!check.get(checkZeroIndex).equals("0")) { 
			checker = checkZeroIndex;
		}
		for(int i = 0; i < 8; i++) {
			String temp = Integer.toString(Integer.parseInt(a.substring(16 * i, (16 * i)+16), 2), 16);
			if(!temp.equals("0")) {
				if(str.equals(":"))
					str = temp + ":";
				else
					str += temp + ":";
			}
			else if(i < checker) {
				str += temp + ":";
			}
		}
		str += ":/" + b;
		long end = System.currentTimeMillis();
		return str;
	}		
	
	public void mapToArray() {
		long start = System.currentTimeMillis();
		Set<Map.Entry<String, Integer>> site = sitesMap.entrySet();
		Set<Map.Entry<String, Integer>> siteSub1 = subSite1Map.entrySet();
		Set<Map.Entry<String, Integer>> siteSub2 = subSite2Map.entrySet();
		Set<Map.Entry<String, Integer>> EU = EUsMap.entrySet();
		
		for(Map.Entry<String, Integer> a : site) {
			String temp = SplitPrint(a.getKey(), a.getValue());
			printerString1.add(temp);
		}
		for(Map.Entry<String, Integer> b : siteSub1) {
			String temp = SplitPrint(b.getKey(), b.getValue());
			printerString2.add(temp);	
		}
		for(Map.Entry<String, Integer> c : siteSub2) {
			String temp = SplitPrint(c.getKey(), c.getValue());
			printerString3.add(temp);	
		}
		for(Map.Entry<String, Integer> d : EU) {
			String temp = SplitPrint(d.getKey(), d.getValue());
			printerString4.add(temp);	
		}
		long end = System.currentTimeMillis();
		System.out.println( "\t-(Message)Map to ArrayList Time : " + ( end - start )/1000.0 );
	}
		
	
	public void FinalDivider() {
		long start = System.currentTimeMillis();
		int jstart = 0; int jend = 0;
		int kstart = 0; int kend = 0;
		int lstart = 0; int lend = 0;
		if(printerString2.size() != 0)
			jend = printerString2.size()/printerString1.size();
		if(printerString3.size() != 0)
			kend = printerString3.size()/printerString2.size();
		if(printerString4.size() != 0)
			lend = printerString4.size()/printerString3.size();
		
		printerString.add(SplitPrint(this.startAddress, this.startSubnet));
		System.out.println("\t-(Message)The server starts the conversion for output.");
		for(int i = 0; i < printerString1.size(); i++) {
				printerString.add(printerString1.get(i));
			for(int j = jstart; j < jend; j++) {
				printerString.add("  " + printerString2.get(j));
				for(int k = kstart; k < kend ; k++) {
					printerString.add("    " + printerString3.get(k));
					for(int l = lstart; l < lend; l++) {
						printerString.add("      " + printerString4.get(l));
					}
					lstart = lend;
					lend += printerString4.size()/printerString3.size();
				}
				kstart = kend;
				kend += printerString3.size()/printerString2.size();
			}
			jstart = jend;
			jend += printerString2.size()/printerString1.size();
		}
		long end = System.currentTimeMillis();
		System.out.println( "\t-(Message)Output Makeing Time : " + ( end - start )/1000.0 );
		System.out.println("\t-(Message)The output has been successfully saved.");
	}
}

class CreateServer extends Thread {
	ServerSocket server;
	Socket socket;
	OutputStream out;
	InputStream in;
	PrintWriter pw;
	BufferedReader br;
	
	String address;
	ArrayList<String> splitedAddress;
	int arrayCount;
	int arrayLocation;
	int subnetMask;
	int sites;
	int subSite1;
	int subSite2;
	int EUs;
	
	Data data;
	
	CreateServer(Socket socket) {
		this.socket = socket;
		data = new Data();
	}
	
	int subnetConversion(int i) {
		if(i > 0 && i <= 2)
			return 1;
		else if(i > 2 && i <= 4)
			return 2;
		else if(i > 4 && i <= 8)
			return 3;
		else if(i > 8 && i <= 16)
			return 4;
		
		return -1;
	}
	
	void splitAddress(){ 
		long start = System.currentTimeMillis();
		splitedAddress = new ArrayList<>(Arrays.asList(address.split(":")));
		if(splitedAddress.isEmpty())
			splitedAddress.add(Integer.toBinaryString(Integer.parseInt("0", 16)));
		int emptyIndex = 0;
		emptyIndex = splitedAddress.indexOf("");
		List<String> tempList = new ArrayList<>();
		if(splitedAddress.contains("")) {
			splitedAddress.set(emptyIndex, "0");
			for(int j = emptyIndex + 1; j < splitedAddress.size(); j++)
				tempList.add(splitedAddress.get(j));
			while(splitedAddress.size() > emptyIndex) {
				splitedAddress.remove(splitedAddress.size() - 1);
			}
		}
		while(true) {
			if(splitedAddress.size() == (8 - tempList.size()))
				break;
			splitedAddress.add(Integer.toBinaryString(Integer.parseInt("0", 16)));
		}
		if(tempList != null)
			splitedAddress.addAll(tempList);
		
		for (int i = 0; i < splitedAddress.size() ; i++) {
			splitedAddress.set(i, Integer.toBinaryString(Integer.parseInt(splitedAddress.get(i), 16)));
			while (splitedAddress.get(i).length() != 16)
				splitedAddress.set(i, "0" + splitedAddress.get(i));		
		}
		long end = System.currentTimeMillis();
		System.out.println( "\t-(Message)Spliting Address Time : " + ( end - start )/1000.0 );
	}
	
	void divideByMask() {
		int temp = subnetMask;
		while(true) {
			if(temp >= 16) {
				temp -= 16;
				arrayCount++;
			}
			else
				break;
		}
		arrayLocation = temp;
	}
	
	void divideForStart() {
		for (int i = 0; i < arrayCount; i++) {
			if(data.startAddress == null)
				data.startAddress = splitedAddress.get(i);
			else
				data.startAddress += splitedAddress.get(i);
		}
		String dv = splitedAddress.get(arrayCount).substring(0, arrayLocation);
		while (dv.length() != 16)
				dv = dv + "0";
		if(data.startAddress == null)
			data.startAddress = dv;
		else
			data.startAddress += dv;
		
		int filling = 128 - data.startAddress.length();
		for(int j = 0; j < filling; j++) {
			data.startAddress += "0";
		}
		
		data.startSubnet = subnetMask;
		System.out.println("\t-(Message)IPv6 address is successfully digitizing.");
	}


		
	
	synchronized void divider(Map<String, Integer> m, int howMany) {
		long start = System.currentTimeMillis();
		int bitsQty = subnetConversion(howMany); // sites number -> bits quantity
		int newMask = subnetMask + bitsQty;
		for(int i = 0; i < howMany; i++) {
			String front = data.startAddress.substring(0, subnetMask);
			String subneting = data.startAddress.substring(subnetMask, subnetMask + bitsQty);
			String end = data.startAddress.substring(subnetMask + bitsQty);
			
			subneting = Integer.toBinaryString(Integer.valueOf(subneting, 2) + i);
			
			while(subneting.length() < bitsQty) {
				if(front.length() > end.length())
					subneting = subneting + "0";
				else
					subneting = "0" + subneting;
			}
			m.put(front + subneting + end, newMask);
		}
		subnetMask = newMask;
		long end = System.currentTimeMillis();
		System.out.println( "\t-(Message)Dividing First Time : " + ( end - start )/1000.0 );
	}
	
	synchronized void dividerForNext(Map<String, Integer> from, Map<String, Integer> to, int howMany) {
		long start = System.currentTimeMillis();
		int bitsQty = subnetConversion(howMany); // sites number -> bits quantity
		int newMask = subnetMask + bitsQty;
		Set<Map.Entry<String, Integer>> entry = from.entrySet();
		
		for(Map.Entry<String, Integer> e : entry) {
			for(int i = 0; i < howMany; i++) {
				String front = e.getKey().substring(0, e.getValue());
				String subneting = e.getKey().substring(e.getValue(), e.getValue() + bitsQty);
				String end = e.getKey().substring(e.getValue() + bitsQty);
				subneting = Integer.toBinaryString(Integer.valueOf(subneting, 2) + i);
				
				while(subneting.length() < bitsQty) {
					if(front.length() > end.length())
						subneting = subneting + "0";
					else
						subneting = "0" + subneting;
				}
				to.put(front + subneting + end, newMask);
			}
			subnetMask = newMask;
		}
		long end = System.currentTimeMillis();
		System.out.println( "\t-(Message)Dividing Next Time : " + ( end - start )/1000.0 );
	}
	
		
	
	public void run() {
		try {			
			System.setProperty("java.net.preferIPv6Addresses","true");
			System.out.println("\t-(Message)Connection from Socket[address=" + socket.getInetAddress() + "/" + socket.getPort() + "/" + socket.getLocalPort() + "]");						
			
 			out = socket.getOutputStream();
			in = socket.getInputStream();
			
			pw = new PrintWriter(new OutputStreamWriter(out));
			br = new BufferedReader(new InputStreamReader(in)); 
		
			
			address = br.readLine();
			splitAddress();
			subnetMask = Integer.parseInt(br.readLine());
			divideByMask();
			sites = Integer.parseInt(br.readLine());
			subSite1 = Integer.parseInt(br.readLine());
			subSite2 = Integer.parseInt(br.readLine());
			EUs = Integer.parseInt(br.readLine());
			System.out.println("\t-(Message)address -> [" + address + "] Subnet Mask -> [" + subnetMask + "]");
			System.out.println("\t-(Message)Sites required -> [" + sites + "] subSite1 required -> [" + subSite1 + "] subSite2 required -> [" + subSite2 + "] EUs required -> [" + EUs + "]");
			divideForStart();
			
			divider(data.sitesMap, sites);	
			dividerForNext(data.sitesMap, data.subSite1Map, subSite1);
			dividerForNext(data.subSite1Map, data.subSite2Map, subSite2);
			dividerForNext(data.subSite2Map, data.EUsMap, EUs);
			
			System.out.println("\t-(Message)Data generation is complete.");
			
			data.mapToArray();
			data.FinalDivider();
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(data.printerString);	
			
			System.out.println("\t-(Message)Data was successfully transferred.");
			System.out.println("\t-(Message)Connection from Socket[address=" + socket.getInetAddress() + "/" + socket.getPort() + "/" + socket.getLocalPort() + "] == logout");
			pw.close();
			br.close();
			socket.close();
		} catch (Exception e) {
			System.out.println("\t-(Message)Connection from Socket[address=" + socket.getInetAddress() + "/" + socket.getPort() + "/" + socket.getLocalPort() + "] == unexpected logout");
			System.out.println("\t-(Message)" + e);
			try {
				socket.close();
				System.out.println("\t-(Message)Close the Socket");
			} catch (Exception socketClose) {
				System.out.println("\t-(Message)Can not close the socket" + socketClose);
			}
			
		}
	}
}

public class Server {
	static Date date;
	static ServerSocket mainServer;
	static Socket socket;
	
	public static void main(String args[]) {
		try {
			mainServer = new ServerSocket(8000);
			date = new Date();
			System.out.println("\t-(Message)MultiThreadSever started at " + date);
			while(true) {
				socket = mainServer.accept();
				CreateServer t = new CreateServer(socket);
				t.start();
			}
		} catch (Exception e) {
			System.out.println("\t-(Message)out " + e);
		}
	}
}	
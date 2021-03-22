import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * Code to implement RR, SJF and SRTF
 * 
 * @author Shariq
 *
 */
public class SchedulingAlgorithms {

	private static List<SchedulingProcess> processes = new ArrayList<>();

	/**
	 * starting point of program. provide an argument which is the file name to be
	 * read
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please provide a file name with full path");
			System.exit(0);
		}
		String filename = args[0];
		readFile(filename);
		if (processes.size() > 8) {
			System.out.println("Input file can have maximum of 8 processes.");
			System.exit(0);
		}
		findAverageTimeInRR(processes.size(), 2, processes);
		findAverageTimeInSJF(processes);
		findaverageTimeSRTF(processes, processes.size());
	}

	/**
	 * reading file
	 * 
	 * @param filename
	 */
	private static void readFile(String filename) {
		try {
			Scanner sc = new Scanner(new File(filename));
			while (sc.hasNext()) {
				Scanner s = new Scanner(sc.nextLine());
				s.useDelimiter(" ");
				while (s.hasNext()) {
					SchedulingProcess obj = new SchedulingProcess();
					obj.setPid(s.next());
					obj.setBurstTime(Integer.parseInt(s.next()));
					obj.setArrivalTime(Integer.parseInt(s.next()));
					processes.add(obj);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * implementing SRTF and finding the desired values
	 * @param proc
	 * @param n
	 * @param wt
	 */
	private static void findWaitingTimeForSRTF(List<SchedulingProcess> proc, int n, int wt[]) {
		int rt[] = new int[n];
		for (int i = 0; i < n; i++) {
			rt[i] = proc.get(i).getBurstTime();
		}
		int complete = 0, t = 0, minm = Integer.MAX_VALUE;
		int shortest = 0, finish_time;
		boolean check = false;
		while (complete != n) {
			for (int j = 0; j < n; j++) {
				if ((proc.get(j).getArrivalTime() <= t) && (rt[j] < minm) && rt[j] > 0) {
					minm = rt[j];
					shortest = j;
					check = true;
					System.out.println(minm+"\t\t"+proc.get(j).getPid()+"\t\t"+"Process preempted by process with shorter burst time");
				}
			}
			if (check == false) {
				t++;
				continue;
			}
			rt[shortest]--;
			minm = rt[shortest];
			if (minm == 0)
				minm = Integer.MAX_VALUE;
			if (rt[shortest] == 0) {
				complete++;
				check = false;
				finish_time = t + 1;
				wt[shortest] = finish_time - proc.get(shortest).getBurstTime() - proc.get(shortest).getArrivalTime();
				if (wt[shortest] < 0)
					wt[shortest] = 0;
				System.out.println(finish_time+"\t\t"+proc.get(shortest).getPid()+"\t\t"+"Process terminated");
			}
			t++;
		}
	}

	/**
	 * driver function to find SRTF 
	 * @param proc
	 * @param n
	 */
	private static void findaverageTimeSRTF(List<SchedulingProcess> proc, int n) {
		int waiting_time[] = new int[n], turnaround_time[] = new int[n];
		int total_wt = 0, total_tat = 0;
		findWaitingTimeForSRTF(proc, n, waiting_time);
		// calculating turn around time
		for (int i = 0; i < n; i++) {
			turnaround_time[i] = proc.get(i).getBurstTime() + waiting_time[i];
		}
		System.out.println("Shortest Remaining Time First");
		System.out.println("Processes ID "  + " Turnaround time " + " Waiting time ");
		for (int i = 0; i < n; i++) {
			total_wt = total_wt + waiting_time[i];
			total_tat = total_tat + turnaround_time[i];
			System.out.println(" " + proc.get(i).getPid() + "\t\t " +turnaround_time[i] + "\t\t" + waiting_time[i]);
		}		
		System.out.println("Average \t" + ((float) total_tat / (float) n) +"\t\t" +  ((float) total_wt / (float) n));
	}

	/**
	 * finding waiting time in round robin taking quantum as 2
	 * @param waiting_time
	 * @param n
	 * @param quantum
	 * @param completion_time
	 * @param processes
	 */
	private static void findWaitingTimeRR(int waiting_time[], int n, int quantum, int completion_time[],
			List<SchedulingProcess> processes) {
		int remaining_time[] = new int[n];
		for (int i = 0; i < waiting_time.length; i++) {
			remaining_time[i] = processes.get(i).getBurstTime();
		}
		int t = 0;
		int arrival = 0;
		while (true) {
			boolean done = true;
			for (int i = 0; i < n; i++) {
				SchedulingProcess process = processes.get(i);
				if (remaining_time[i] > 0) {
					done = false;
					if (remaining_time[i] > quantum && process.getArrivalTime() <= arrival) {
						int temp = t;
						t += quantum;
						remaining_time[i] -= quantum;
						arrival++;
						System.out.println(temp+"\t\t"+process.getPid()+"\t\t"+"Quantum expired");
					} else {
						if (process.getArrivalTime() <= arrival) {
							int temp = t;
							arrival++;
							t += remaining_time[i];
							remaining_time[i] = 0;
							completion_time[i] = t;
							System.out.println(temp+"\t\t"+process.getPid()+"\t\t"+"Process terminated");
						}
					}
				}
			}
			if (done == true) {
				break;
			}
		}
	}

	/**
	 * driver program to implement RR
	 * @param n
	 * @param quantum
	 * @param processes
	 */
	private static void findAverageTimeInRR(int n, int quantum, List<SchedulingProcess> processes) {
		int waiting_time[] = new int[n];
		int turnaround_time[] = new int[n];
		int completion_time[] = new int[n];
		findWaitingTimeRR(waiting_time, n, quantum, completion_time, processes);
		// calculating turn around time here
		for (int i = 0; i < n; i++) {
			SchedulingProcess process = processes.get(i);
			turnaround_time[i] = completion_time[i] - process.getArrivalTime();
			waiting_time[i] = turnaround_time[i] - process.getBurstTime();
		}
		int total_wt = 0, total_tat = 0;
		System.out.println("Round Robin Scheduling");
		System.out.println("Process ID " + " Turn Around Time " + " Waiting time");
		for (int i = 0; i < n; i++) {
			total_wt = total_wt + waiting_time[i];
			total_tat = total_tat + turnaround_time[i];
			SchedulingProcess process = processes.get(i);
			System.out.println(" " +process.getPid() + "\t\t" + turnaround_time[i] + "\t\t " + waiting_time[i]);
		}
		System.out.println("Average \t" + ((float) total_tat / (float) n)+"\t\t"+((float) total_wt / (float) n));
	}

	/**
	 * Implementing SJF
	 * @param processes
	 */
	public static void findAverageTimeInSJF(List<SchedulingProcess> processes) {
		int n = processes.size();
		int total_process_counter = 0;
		int shortest_time = 0;
		int completion_status_flags[] = new int[n];
		int completion_time[] = new int[n];
		int waiting_time[] = new int[n];
		int turnaround_time[] = new int[n];
		while (true) {
			int c = n, min = Integer.MAX_VALUE;
			if (total_process_counter == n)
				break;
			for (int i = 0; i < n; i++) {
				SchedulingProcess process = processes.get(i);
				if ((process.getArrivalTime() <= shortest_time) && (completion_status_flags[i] == 0) && (process.getBurstTime() < min)) {
					min = process.getBurstTime();
					c = i;
				}
			}
			if (c == n) {
				shortest_time++;
			}
			else {
				completion_time[c] = shortest_time + processes.get(c).getBurstTime();
				shortest_time += processes.get(c).getBurstTime();
				turnaround_time[c] = completion_time[c] - processes.get(c).getArrivalTime();
				waiting_time[c] = turnaround_time[c] - processes.get(c).getBurstTime();
				completion_status_flags[c] = 1;
				total_process_counter++;
				System.out.println(shortest_time+"\t\t"+processes.get(c).getPid()+"\t\t"+"Process terminated");
			}
		}
		float avgwt = 0, avgta = 0;
		System.out.println("Shortest Job First Scheduling");
		System.out.println("Process ID " + " Turn Around Time " + " Waiting time");
		for (int i = 0; i < n; i++) {
			SchedulingProcess process = processes.get(i);
			avgwt += waiting_time[i];
			avgta += turnaround_time[i];
			System.out.println(process.getPid() + "\t\t" + turnaround_time[i] + "\t\t" + waiting_time[i]);
		}
		System.out.println("Average \t" + (float) (avgta / n) + "\t\t" +(float) (avgwt / n) );
	}
}

/**
 * class to represent process
 * 
 * @author Shariq
 *
 */
class SchedulingProcess {
	private String pid;
	private int burstTime;
	private int arrivalTime;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getBurstTime() {
		return burstTime;
	}

	public void setBurstTime(int burstTime) {
		this.burstTime = burstTime;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	@Override
	public String toString() {
		return "SchedulingProcess [pid=" + pid + ", burstTime=" + burstTime + ", arrivalTime=" + arrivalTime + "]";
	}
}
import java.util.*;

public class RoundRobins {

    protected PriorityQueue<JobObject> jobs = new PriorityQueue<>(new FIFOComparator());
    protected Queue<JobObject> ready = new LinkedList<>();
    protected Deque<JobObject> running = new ArrayDeque<>();
    protected PriorityQueue<JobObject> complete = new PriorityQueue<>(new FIFOComparator());
    protected double totalTurnAround = 0;
    protected double count = 0;
    protected int timer = 0;
    Random rand = new Random();
    protected int timeQuantum = rand.nextInt(3)+1;
    protected  int contextSwitch = rand.nextInt(2)+1;

    //default constructor
    public RoundRobins() {

    }

    /**
     *
     * @param jobs the array of jobs which will be put in a priority queue
     */
    public RoundRobins(JobObject[] jobs) {
        JobObject[] jobsRR = new JobObject[jobs.length];
        for(int i = 0; i < jobs.length; i++) {
            jobsRR[i] = new JobObject(jobs[i].arrTime, jobs[i].cpuBurst, jobs[i].priority, jobs[i].remainingTime);
        }
        this.jobs.addAll(Arrays.asList(jobsRR));
    }

    public void addTaskArrivedDuringCS(int prevTime,int currTime) {
        while (!jobs.isEmpty() && prevTime <= jobs.peek().arrTime  && jobs.peek().arrTime <= currTime) {
            JobObject readyJob = jobs.poll();
            ready.add(readyJob);
            readyJob.remainingTimes.add(readyJob.remainingTime);
        }
    }

    public void addToReadyQueue(int currTime) {
        while (!jobs.isEmpty() && currTime == jobs.peek().arrTime) {
            JobObject readyJob = jobs.poll();
            ready.add(readyJob);
            readyJob.remainingTimes.add(readyJob.remainingTime);
        }
    }

    public void setJobTimes(){
        while(!jobs.isEmpty()) {
            int timeLapse = 0;
            JobObject current = null;
            if (timer == jobs.peek().arrTime) {
                current = jobs.poll();
                current.remainingTimes.add(current.remainingTime);
                running.push(current); // add to running only if it's time arrives
            }

            //while ready stack is non empty or current job is still within time quantum
            while(timeLapse <= timeQuantum && (!ready.isEmpty() ||!running.isEmpty() || (!jobs.isEmpty()  && timer == jobs.peek().arrTime)) ) {

                if(ready.isEmpty() && running.isEmpty()) {
                    current = jobs.poll();
                    running.push(current);
                    timeLapse = 0;
                }
                //while there are ready objects push it to ready stack
                addToReadyQueue(timer);

                //if the job has completed
                if(current.remainingTime == 0) {
                    current.remainingTimes.add(current.remainingTime); // add to rt array
                    current.exitTime = timer; //exit time
                    current.turnAroundTime = current.exitTime - current.arrTime; //turn around time
                    totalTurnAround += current.turnAroundTime;
                    running.pop(); // pop it off the running stack
                    complete.add(current); // add it to completed stack
                    //keeps track of completed jobs within time 100 for through put
                    if(timer <= 100) {
                        count+=1;
                    }
                    int prevTime = timer;
                    timer+=contextSwitch;

                    addTaskArrivedDuringCS(prevTime,timer);
                    if(!ready.isEmpty()){
                        current = ready.poll(); // update current
                        running.push(current); // push it to running stack
                    }

                    timeLapse = 0; // reset time lapse
                }
                else if(timeLapse == timeQuantum && !running.isEmpty()) { // if the time limit has reached
                    current.remainingTimes.add(current.remainingTime);
                    running.pop(); //take it off the running stack
                    JobObject old_curr = current;
                    ready.add(old_curr); // add the most recent job that has been popped off of running stack

                    int prevTime = timer;
                    timer+=contextSwitch;

                    addTaskArrivedDuringCS(prevTime,timer);
                    if(!ready.isEmpty()){
                        current = ready.poll(); // update current
                        running.push(current); // push it to running stack

                    }
                    timeLapse = 0;
                }

                timer+=1;
                current.remainingTime -= 1;
                timeLapse+=1;

            }
            timer+=1;


        }

    }

    public void print() {
        System.out.println("Round Robin Scheduling with context switch");
        System.out.println("Time Quantum " + timeQuantum);
        System.out.println("Context Switch " + contextSwitch);
        System.out.println("| Arrival time\t| CPU Burst\t| Exit\t| Turn Around Time\t| Remaining Time\t");
        while (!complete.isEmpty()) {
            JobObject job = complete.poll();
            System.out.print("|\t\t"+job.arrTime + "\t\t|\t");
            System.out.print(job.cpuBurst + "\t\t|\t");
            System.out.print(job.exitTime + "\t|\t");
            System.out.print("\t"+ job.turnAroundTime + "\t\t\t|\t");
            for(int time: job.remainingTimes) {
                System.out.print(time + " ");
            }
            System.out.println("\t\t\t");
        }

        System.out.println("Average turn around time: " + totalTurnAround/count);
        System.out.println("Through-put: " + count/100);
        System.out.println();
    }
}

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;

public class SJF {

    protected PriorityQueue<JobObject> jobs = new PriorityQueue<>(new FIFOComparator());
    protected PriorityQueue<JobObject> ready = new PriorityQueue<>(new SJFComparator());
    protected Deque<JobObject> running = new ArrayDeque<>();
    protected PriorityQueue<JobObject> complete = new PriorityQueue<>(new FIFOComparator());
    protected double totalTurnAround = 0;
    protected double count = 0;
   protected int lastExit = 0;
   protected   int timer = 0;


    //default constructor
    public SJF() {

    }

    /**
     *
     * @param jobs the array of jobs which will be put in a priority queue
     */
    public SJF(JobObject[] jobs) {
        JobObject[] jobsSJF = new JobObject[jobs.length];
        for(int i = 0; i < jobs.length; i++) {
            jobsSJF[i] = new JobObject(jobs[i].arrTime, jobs[i].cpuBurst, jobs[i].priority, jobs[i].remainingTime);
        }
        this.jobs.addAll(Arrays.asList(jobsSJF));
    }


    public void setJobTimes(){
        //int completed = 0;

        // run till there is no more job left in the job stack
        while(!jobs.isEmpty()) {
            JobObject current = null;
            JobObject next = null;
            if(timer == jobs.peek().arrTime) {
                current = jobs.poll();
                running.push(current); // add to running only if it's time arrives
            }

            //if there is more than one job, then next job is the job right after current in the stack
            if(!jobs.isEmpty()) {
                next = jobs.peek();
            }
            //while something is running continue the loop

            while (!running.isEmpty() || (!jobs.isEmpty()  && timer == jobs.peek().arrTime) ) {

                if(ready.isEmpty() && running.isEmpty()) {
                    current = jobs.poll();
                    running.push(current);
                }
                while(next != null && timer == next.arrTime) {
                    ready.add(jobs.poll()); // add the newly arrived job to readylist
                    if(!jobs.isEmpty()) {
                        next = jobs.peek(); // updating the next job
                    }
                    else{
                        next = null; //no more job left
                    }
                }

                //check if the current running job is completed or not
                if (current.remainingTime == 0) {
                    current.exitTime = timer;
                    lastExit = current.exitTime;
                    current.turnAroundTime = current.exitTime - current.arrTime;
                    totalTurnAround += current.turnAroundTime;
                    complete.add(current); // add to completed stack
                    running.pop();
                    if(timer <= 100) {
                        count+=1;
                    }
                    //if job and ready queue is non empty and the next job in job stack has arrived
                    if (!jobs.isEmpty() && !ready.isEmpty() && jobs.peek().arrTime == current.exitTime) {
                        // if the newly arrived job takes less time to execute
                        if (jobs.peek().remainingTime < ready.peek().remainingTime) {
                            current = jobs.poll(); // set the current job to that and push it to running stack
                            running.push(current);
                            //if there is more job left in the stack, set the next to it
                        } else {
                            current = ready.poll(); //otherwise set current job from ready queue and push to running stack
                            running.push(current);
                            ready.add(jobs.poll());
                        }
                        if(!jobs.isEmpty()) {
                            next = jobs.peek();
                        }
                        else {
                            next = null;
                        }
                    } else if (!jobs.isEmpty() && ready.isEmpty() && jobs.peek().arrTime == current.exitTime) {
                        //if there is no job readying and new job has arrived to job stack then push the current job from job stack
                        current = jobs.poll();
                        running.push(current);
                    } else if (!ready.isEmpty()) {
                        // if a job is readying push the least remaining time job to running stack
                        current = ready.poll();
                        running.push(current);
                    }

                }
                timer += 1;
                current.remainingTime -= 1;

            }
            timer += 1;

        }

    }

    public void print() {
        System.out.println("SJF Scheduling");
        System.out.println("| Arrival time\t| CPU Burst\t| Exit\t| Turn Around Time\t|");
        while (!complete.isEmpty()) {
            JobObject job = complete.poll();
            System.out.print("|\t\t"+job.arrTime + "\t\t|\t");
            System.out.print(job.cpuBurst + "\t\t|\t");
            System.out.print(job.exitTime + "\t|\t");
            System.out.println("\t"+ job.turnAroundTime + "\t\t\t|\t");
        }

        System.out.println("Average turn around time: " + totalTurnAround/count);
        System.out.println("Through-put: " + count/100);
        System.out.println();
    }
}

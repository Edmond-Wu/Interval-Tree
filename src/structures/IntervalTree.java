package structures;

import java.util.*;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		Sorter.sortIntervals(intervalsLeft, 'l');
		Sorter.sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = Sorter.getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
		
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */

	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		for (int x = 0; x < endPoints.size(); x++) {
			System.out.print(endPoints.get(x) + " ");
		}
		System.out.println();
		
		Queue<IntervalTreeNode> Q = new Queue<IntervalTreeNode>();
		
		for (int i = 0; i < endPoints.size(); i++) {
			Q.enqueue(new IntervalTreeNode((float)endPoints.get(i), (float)endPoints.get(i), (float)endPoints.get(i)));
		}
		
		int s = Q.size();
		
		while(s > 0) {
			if (s == 1) {
				return Q.dequeue();
			}
			else {
				int temps = s;
				while (temps > 1) {
					IntervalTreeNode T1 = Q.dequeue();
					IntervalTreeNode T2 = Q.dequeue();
					float v1 = T1.maxSplitValue;
					float v2 = T2.minSplitValue;
					float x = (v1 + v2)/2;
					IntervalTreeNode N = new IntervalTreeNode(x, T1.minSplitValue, T2.maxSplitValue);
					N.leftChild = T1;
					N.rightChild = T2;
					//System.out.println(N.splitValue);
					Q.enqueue(N);
					temps -= 2;
				}
				
				if (temps == 1) {
					Q.enqueue(Q.dequeue());
				}
				s = Q.size();
			}
		}
		return Q.dequeue();
	}

	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * Let the interval tree constructed in Step 6 be T
          for each interval [x,y] in Lsort do 
              starting at the root, 
                  search in the interval tree for the first (highest) node, N, whose split value is contained in [x,y]
              add [x,y] to the LEFT LIST of node N
          endfor
          for each interval [x,y] in Rsort do 
             starting at the root, 
                  search in the interval tree for the first (highest) node, N, whose split value is contained in [x,y]
             add [x,y] to the RIGHT LIST of node N
          endfor
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */

	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		IntervalTreeNode tempLeft = getRoot();
		
		for (int i = 0; i < leftSortedIntervals.size(); i++) {	
			boolean added = false;
			
			while (tempLeft != null && !added) {
				if (tempLeft.splitValue >= leftSortedIntervals.get(i).leftEndPoint && tempLeft.splitValue <= leftSortedIntervals.get(i).rightEndPoint) {
					if (tempLeft.leftIntervals == null) {
						tempLeft.leftIntervals = new ArrayList<Interval>();
					}
					tempLeft.leftIntervals.add(leftSortedIntervals.get(i));
					tempLeft = root;
					added = true;
				}
				else if (tempLeft.splitValue < leftSortedIntervals.get(i).leftEndPoint) {
					tempLeft = tempLeft.rightChild;
				}
				else {
					tempLeft = tempLeft.leftChild;
				}
			}
		}
		
		IntervalTreeNode tempRight = getRoot();
		
		for (int j = 0; j < rightSortedIntervals.size(); j++) {
			boolean added = false;
			while (tempRight != null && !added) {
				if (tempRight.splitValue >= rightSortedIntervals.get(j).leftEndPoint && tempRight.splitValue <= rightSortedIntervals.get(j).rightEndPoint) {
					if (tempRight.rightIntervals == null) {
						tempRight.rightIntervals = new ArrayList<Interval>();
					}
					tempRight.rightIntervals.add(rightSortedIntervals.get(j));
					tempRight = root;
					added = true;
				}
				else if (tempRight.splitValue < rightSortedIntervals.get(j).leftEndPoint) {
					tempRight = tempRight.rightChild;
				}
				else {
					tempRight = tempRight.leftChild;
				}
			}
		}
	}
	
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {	
		//printIntervals(root.leftIntervals);
		ArrayList<Interval> resultList = new ArrayList<Interval>();
		if (root.leftChild == null && root.rightChild == null) {
			return resultList;
		}
		float split = root.splitValue;
		IntervalTreeNode rsub = root.rightChild;
		IntervalTreeNode lsub = root.leftChild;
		if (split >= q.leftEndPoint && split <= q.rightEndPoint) {
			if (root.leftIntervals != null) {
				resultList.addAll(root.leftIntervals);
			}
			if (lsub != null) {
				ArrayList<Interval> temp = query(lsub, q, resultList);
				if (temp != null) {
					resultList.addAll(temp);
				}
			}
			if (rsub != null) {
				ArrayList<Interval> temp = query(rsub, q, resultList);
				if (temp != null) {
					resultList.addAll(temp);
				}			
			}
		}
		else if (split < q.leftEndPoint)
		{
			if (root.leftIntervals != null) {
				for (int i = 0; i < root.leftIntervals.size(); i++) {
					if (isIntersecting(q, root.leftIntervals.get(i))) {
						resultList.add(root.leftIntervals.get(i));
					}
				}
			}
			if (rsub != null) {
				ArrayList<Interval> temp = query(rsub, q, resultList);
				if (temp != null) {
					resultList.addAll(temp);
				}			
			}
		}
		else {
			if (root.leftIntervals != null) {
				for (int i = 0; i < root.leftIntervals.size(); i++) {
					if (isIntersecting(q, root.leftIntervals.get(i))) {
						resultList.add(root.leftIntervals.get(i));
					}
				}
			}
			if (lsub != null) {
				ArrayList<Interval> temp = query(lsub, q, resultList);
				if (temp != null) {
					resultList.addAll(temp);
				}
			}
		}
		return resultList;
	}
	
	/**
	 * Adds intervals to the query and returns the updated query, null otherwise
	 * @param r root
	 * @param q interval to be compared to
	 * @param query
	 * @return
	 */
	private ArrayList<Interval> query(IntervalTreeNode r, Interval q, ArrayList<Interval> query) {
		if (r.leftChild == null && r.rightChild == null) {
			return query;
		}
		IntervalTreeNode rsub = r.rightChild;
		IntervalTreeNode lsub = r.leftChild;
		float split = r.splitValue;
		
		if (split >= q.leftEndPoint && split <= q.rightEndPoint) {
			if (r.leftIntervals != null) {
				query.addAll(r.leftIntervals);
			}
			if (lsub != null) {
				query(lsub, q, query);
			}
			if (rsub != null) {
				query(rsub, q, query);
			}
		}
		else if (split < q.leftEndPoint) {
			if (r.rightIntervals != null) {
				int i = r.rightIntervals.size() - 1;
				while (i >= 0 && isIntersecting(q, r.rightIntervals.get(i))) {
					query.add(r.rightIntervals.get(i));
					i--;
				}
				if (rsub != null) {
					query(rsub, q, query);
				}
			}
		}
		else if (split > q.rightEndPoint) {
			int i = 0;
			if (r.leftIntervals != null) {
				while (i < r.leftIntervals.size() && isIntersecting(q, r.leftIntervals.get(i))) {
					query.add(r.leftIntervals.get(i));
					i++;
				}
			}
			if (lsub != null) {
				query(lsub, q, query);
			}
		}
		return null;
	}
	
	/**
	 * Checks if 2 intervals are intersecting
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean isIntersecting(Interval a, Interval b) {
		int beginA = a.leftEndPoint, endA = a.rightEndPoint;
		int beginB = b.leftEndPoint, endB = b.rightEndPoint;
		
		return !(endA < beginB || endB < beginA);
	}

	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
}


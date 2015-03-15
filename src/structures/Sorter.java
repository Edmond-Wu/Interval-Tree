package structures;

import java.util.ArrayList;

/**
 * This class is a repository of sorting methods used by the interval tree.
 * It's a utility class - all methods are static, and the class cannot be instantiated
 * i.e. no objects can be created for this class.
 * 
 * @author runb-cs112
 */
public class Sorter 
{
	private Sorter() { }
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) 
	{
		if (lr != 'l' && lr != 'r')
			return;
		
		if (intervals.size() == 0)
			return;
		
		else
		{
			if (lr == 'l')
			{
				for (int i=1; i<intervals.size(); i++)
				{
					Interval inserted = intervals.get(i);
					int j = i-1;
					while (j >= 0 && intervals.get(j).leftEndPoint > inserted.leftEndPoint)
					{
						intervals.set(j+1, intervals.get(j));
						j--;
					}
					intervals.set(j+1, inserted);
				}
			}
			else
			{
				for (int i=1; i<intervals.size(); i++)
				{
					Interval inserted = intervals.get(i);
					int j = i-1;
					while (j >= 0 && intervals.get(j).rightEndPoint > inserted.rightEndPoint)
					{
						intervals.set(j+1, intervals.get(j));
						j--;
					}
					intervals.set(j+1, inserted);
				}
			}
		}
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) 
	{
		ArrayList<Integer> points = new ArrayList<Integer>();
		
		for (int i = 0; i < leftSortedIntervals.size(); i++)
		{
			points.add(leftSortedIntervals.get(i).leftEndPoint);
		}
		
		for (int i = 0; i < points.size() - 2; i++)
		{
			while (points.get(i) == points.get(i + 1))
			{
				points.remove(i + 1);
			}
		}
		
		for (int i = 0; i < rightSortedIntervals.size(); i++)
		{
			if (!points.contains(rightSortedIntervals.get(i).rightEndPoint))
			{
				points.add(rightSortedIntervals.get(i).rightEndPoint);
			}
		}
		
		for (int i = 1; i < points.size(); i++)
		{
			int inserted = points.get(i);
			int j = i - 1;
			while (j >= 0 && points.get(j) > inserted){
				points.set(j + 1, points.get(j));
				j--;
			}
			points.set(j + 1, inserted);
		}
		
		return points;
	}
}

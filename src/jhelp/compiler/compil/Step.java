package jhelp.compiler.compil;

import java.util.ArrayList;
import java.util.List;

/**
 * A progress step
 *
 * @author JHelp <br>
 */
class Step
      implements Comparable<Step>
{
   /** Current path */
   private final List<StackInfo>    path;
   /** Stack status on step */
   private final List<StackElement> status;
   /** Index in instruction list */
   final int                        index;

   /**
    * Create a new instance of Step
    *
    * @param index
    *           Index in instruction list
    * @param status
    *           Stack status on step
    * @param path
    *           Current path
    */
   public Step(final int index, final List<StackElement> status, final List<StackInfo> path)
   {
      this.index = index;
      this.status = new ArrayList<StackElement>();
      this.status.addAll(status);
      this.path = new ArrayList<StackInfo>();
      this.path.addAll(path);
   }

   /**
    * Compare with an other step to order them <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param step
    *           Step to compare with
    * @return Comparison result
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(final Step step)
   {
      return this.index - step.index;
   }

   /**
    * Indicates if given object equals to this step <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param object
    *           Object to compare with
    * @return {@code true} if given object equals to this step
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object object)
   {
      if(this == object)
      {
         return true;
      }

      if(null == object)
      {
         return false;
      }

      if(!Step.class.equals(object.getClass()))
      {
         return false;
      }

      return this.index == ((Step) object).index;
   }

   /**
    * Hash code <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @return Hash code
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      return this.index;
   }

   /**
    * String representation <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @return String representation
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.index + ":" + this.status;
   }

   /**
    * Transfer step information
    *
    * @param status
    *           Status where transfer this step status
    * @param path
    *           Path where transfer this step path
    */
   public void transferStatus(final List<StackElement> status, final List<StackInfo> path)
   {
      status.clear();
      status.addAll(this.status);
      path.clear();
      path.addAll(this.path);
   }
}
package jhelp.compiler.compil;

/**
 * Select case : match associated with a label
 *
 * @author JHelp <br>
 */
class SelectCase
      implements Comparable<SelectCase>
{
   /** Label to go */
   private final String label;
   /** Value to match */
   private final int    match;

   /**
    * Create a new instance of SelectCase
    *
    * @param match
    *           Value to match
    * @param label
    *           Label to go
    */
   public SelectCase(final int match, final String label)
   {
      this.match = match;
      this.label = label;
   }

   /**
    * Compare with an other select case to know with one is before the other.<br>
    * It returns
    * <table border=0>
    * <tr>
    * <th>&lt; 0</th>
    * <td>If this case before given one</td>
    * </tr>
    * <tr>
    * <th>0</th>
    * <td>If this case equals to given one</td>
    * </tr>
    * <tr>
    * <th>&gt; 0</th>
    * <td>If this case after given one</td>
    * </tr>
    * </table>
    * <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    *
    * @param selectCase
    *           Case to compare with
    * @return Comparison result
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(final SelectCase selectCase)
   {
      return this.match - selectCase.match;
   }

   /**
    * Label to go
    *
    * @return Label to go
    */
   public String getLabel()
   {
      return this.label;
   }

   /**
    * Value to match
    *
    * @return Value to match
    */
   public int getMatch()
   {
      return this.match;
   }
}
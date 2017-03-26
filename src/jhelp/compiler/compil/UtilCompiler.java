package jhelp.compiler.compil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;

import jhelp.util.debug.Debug;
import jhelp.util.list.Pair;
import jhelp.util.resources.ResourceElement;
import jhelp.util.resources.Resources;
import jhelp.util.resources.ResourcesSystem;

/**
 * Utilities for compiler
 *
 * @author JHelp <br>
 */
public class UtilCompiler
{
   /** java.lang package's classes list */
   private static final List<String> JAVA_LANG_CLASSES = UtilCompiler.obtainJavaLangClasses();

   /**
    * Collect java.lang package's classes
    *
    * @return java.lang package's classes list
    */
   private static List<String> obtainJavaLangClasses()
   {
      final List<String> list = new ArrayList<String>();

      try
      {
         final Resources resources = new Resources(String.class);
         final ResourcesSystem resourcesSystem = resources.obtainResourcesSystem();
         String name;

         for(final ResourceElement resourceElement : resourcesSystem.obtainList(ResourcesSystem.ROOT))
         {
            name = resourceElement.getName();

            if(name.endsWith(".class"))
            {
               list.add(name.substring(0, name.length() - 6));
            }
         }
      }
      catch(final Exception exception)
      {
         Debug.printException(exception, "Failed to get java.lang class list !");
      }

      return Collections.unmodifiableList(list);
   }

   /**
    * Indicates if given class short name inside java.lang package
    *
    * @param name
    *           Class short name
    * @return {@code true} if given class short name inside java.lang package
    */
   public static boolean isJavaLangClass(final String name)
   {
      return UtilCompiler.JAVA_LANG_CLASSES.contains(name);
   }

   /**
    * Obtain the first instruction handle with corresponding line number is greater or equal to given line number
    *
    * @param lineNumber
    *           Line number search
    * @param linesTable
    *           Lines table
    * @return Found handle
    */
   public static InstructionHandle obtainInstructionAtOrAfter(final int lineNumber, final List<Pair<InstructionHandle, Integer>> linesTable)
   {
      for(final Pair<InstructionHandle, Integer> pair : linesTable)
      {
         if(pair.element2 >= lineNumber)
         {
            return pair.element1;
         }
      }

      return linesTable.get(0).element1;
   }

   /**
    * Obtain the last instruction handle with corresponding line number is lower or equal to given line number
    *
    * @param lineNumber
    *           Line number search
    * @param linesTable
    *           Lines table
    * @return Found handle
    */
   public static InstructionHandle obtainIntstructionAtOrBefore(final int lineNumber, final List<Pair<InstructionHandle, Integer>> linesTable)
   {
      Pair<InstructionHandle, Integer> pair;

      for(int i = linesTable.size() - 1; i >= 0; i--)
      {
         pair = linesTable.get(i);

         if(pair.element2 <= lineNumber)
         {
            return pair.element1;
         }
      }

      return linesTable.get(linesTable.size() - 1).element1;
   }
}
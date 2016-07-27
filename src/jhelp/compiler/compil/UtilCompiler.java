package jhelp.compiler.compil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jhelp.util.debug.Debug;
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

            if(name.endsWith(".class") == true)
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
}
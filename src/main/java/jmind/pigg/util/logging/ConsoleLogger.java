/*
 *  
 *
 * The jmind-pigg Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jmind.pigg.util.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xieweibo
 */
public class ConsoleLogger extends AbstractInternalLogger {

  private InternalLogLevel level ;

  protected ConsoleLogger(String name) {
    super(name);
    this.level=InternalLogLevel.WARN;
  }



  public void setLevel(InternalLogLevel level){
    this.level=level;
  }

  @Override
  public boolean isTraceEnabled() {
   return level.equals(InternalLogLevel.TRACE);
  }

  @Override
  public void trace(String msg) {
    if(isTraceEnabled())
      println(msg);
  }

  @Override
  public void trace(String format, Object arg) {
    if(isTraceEnabled())
      println(format,arg);
  }

  @Override
  public void trace(String format, Object argA, Object argB) {
    if(isTraceEnabled())
      println(format,argA,argB);
  }

  @Override
  public void trace(String format, Object... arguments) {
    if(isTraceEnabled())
      println(format,arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    if(isTraceEnabled())
    println(msg, t);
  }

  @Override
  public boolean isDebugEnabled() {
    return level.compareTo(InternalLogLevel.DEBUG)>=0;
  }

  @Override
  public void debug(String msg) {
    if(isDebugEnabled())
      println(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    if(isDebugEnabled())
      println(format, arg);
  }

  @Override
  public void debug(String format, Object argA, Object argB) {
    if(isDebugEnabled())
      println(format, argA, argB);
  }

  @Override
  public void debug(String format, Object... arguments) {
    if(isDebugEnabled())
      println(format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    if(isDebugEnabled())
      println(msg, t);
  }

  @Override
  public boolean isInfoEnabled() {
     return level.compareTo(InternalLogLevel.INFO)>=0;
  }

  @Override
  public void info(String msg) {
    if(isInfoEnabled())
      println(msg);
  }

  @Override
  public void info(String format, Object arg) {
    if(isInfoEnabled())
      println(format, arg);
  }

  @Override
  public void info(String format, Object argA, Object argB) {
    if(isInfoEnabled())
      println(format, argA, argB);
  }

  @Override
  public void info(String format, Object... arguments) {
    if(isInfoEnabled())
      println(format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    if(isInfoEnabled())
    println(msg, t);
  }

  @Override
  public boolean isWarnEnabled() {
    return level.compareTo(InternalLogLevel.WARN)>=0;
  }

  @Override
  public void warn(String msg) {
    if(isWarnEnabled())
    println(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    if(isWarnEnabled())
    println(format, arg);
  }

  @Override
  public void warn(String format, Object... arguments) {
    if(isWarnEnabled())
    println(format, arguments);
  }

  @Override
  public void warn(String format, Object argA, Object argB) {
    if(isWarnEnabled())
    println(format, argA, argB);
  }

  @Override
  public void warn(String msg, Throwable t) {
    if(isWarnEnabled())
      println(msg, t);
  }

  @Override
  public boolean isErrorEnabled() {
    return level.compareTo(InternalLogLevel.ERROR)>=0;
  }

  @Override
  public void error(String msg) {
    println(msg);
  }

  @Override
  public void error(String format, Object arg) {
    println(format, arg);
  }

  @Override
  public void error(String format, Object argA, Object argB) {
    println(format, argA, argB);
  }

  @Override
  public void error(String format, Object... arguments) {
    println(format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    println(msg, t);
  }

  private void println(String msg) {
    System.out.println(formatDate(new Date()) + " [" + Thread.currentThread() + "] " + msg);
  }

  private void println(String msg, Throwable t) {
    System.err.println(formatDate(new Date()) + " [" + Thread.currentThread() + "] " + msg);
    t.printStackTrace();

  }
  private void println(String format, Object... arguments) {
    FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
    println4FormattingTuple(ft);
  }



  private void println4FormattingTuple(FormattingTuple ft) {
    if (ft.getThrowable() != null) {
      ft.getThrowable().printStackTrace();
    } else {
      println(ft.getMessage());
    }
  }

  private String formatDate(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
    return format.format(date);
  }

}

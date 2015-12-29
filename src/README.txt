README for building the forms runtime source.

You may freely use the forms runtime source in your software subject to
the license at the end of this file.

The forms runtime source is dependent on two external libraries: 
1. The JGoodies FormLayout. 
   http://www.jgoodies.com,  https://forms.dev.java.net

   This is an open source layout manager for Java.
   The forms runtime depends on version 1.0.5 of the FormLayout.  The source
   for the FormLayout is included in the formsrt_source.zip.

2. Batik SVG Toolkit.
   http://xml.apache.org/batik/

   This is an open source library for SVG files.  It is used by the forms
   runtime for its LinearGradient and RadialGradient implementations. 
   The source for this toolkit is not included.  The class files are included
   in batik-awt-util.jar.


To build the forms runtime from the source, you only need to include the 
batik-awt-util.jar file in your classpath.


Forms Runtime Source License:

Copyright (c) 2016 Jeff Tassin. Rights Reserved.
 
Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:
  
 o Redistributions of source code must retain the above copyright notice, 
   this list of conditions and the following disclaimer. 
      
 o Redistributions in binary form must reproduce the above copyright notice, 
   this list of conditions and the following disclaimer in the documentation 
   and/or other materials provided with the distribution. 
      
 o Neither the name of JETA Software, Inc. nor the names of 
   its contributors may be used to endorse or promote products derived 
   from this software without specific prior written permission. 
      
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 

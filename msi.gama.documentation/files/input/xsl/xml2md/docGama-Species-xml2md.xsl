<?xml version="1.0" encoding="UTF-8"?><!---->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wiki="www.google.fr">

<xsl:template match="/">
 	<xsl:text># Built-in Species
 	
----
**This file is automatically generated from java files. Do Not Edit It.**

----


It is possible to use in the models a set of built-in agents. These agents allow to directly use some advance features like clustering, multi-criteria analysis, etc. The creation of these agents are similar as for other kinds of agents:

```
create species: my_built_in_agent returns: the_agent;```
    
So, for instance, to be able to use clustering techniques in the model:
```
create cluster_builder returns: clusterer;```

[Top of the page](#table-of-contents) 

	</xsl:text>


## Table of Contents
&lt;wiki:toc max_depth="3" /&gt;
<xsl:call-template name="buildSpeciesByName"/>

<xsl:call-template name="buildSpecies"/>

</xsl:template>

<xsl:template name="buildSpecies">

	<xsl:for-each select="doc/speciess/species">
    	<xsl:sort select="@name" />
    	
----

## `<xsl:value-of select="@name"/>`	

### Actions
	<xsl:for-each select="actions/action">		
	<xsl:sort select="@name" />  
	 
#### **`<xsl:value-of select="@name"/>`**
<xsl:value-of select="documentation/result"/>
* returns: <xsl:value-of select="@returnType"/>
<xsl:for-each select="args/arg"> 			
* → **`<xsl:value-of select="@name"/>`** (<xsl:value-of select="@type"/>): <xsl:value-of select="documentation/result"/> 
</xsl:for-each>

<xsl:if test="documentation/examples[node()]">

```
<xsl:for-each select="documentation/examples/example" >
<xsl:if test="@code != ''"><xsl:value-of select="@code"/><xsl:text>
</xsl:text>
</xsl:if>
</xsl:for-each>```
</xsl:if>	
		
</xsl:for-each>			

[Top of the page](#table-of-contents) 
	</xsl:for-each>
</xsl:template>
  
<xsl:template name="buildSpeciesByName">
	<xsl:for-each select="/doc/speciess/species"> 
		<xsl:sort select="@name" />
			<xsl:text>[</xsl:text> <xsl:value-of select="@name"/> <xsl:text>](#</xsl:text> <xsl:value-of select="@name"/> <xsl:text>), </xsl:text> 
	</xsl:for-each>  
</xsl:template>  
  
</xsl:stylesheet>

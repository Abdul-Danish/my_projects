<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:j="http://www.w3.org/2005/xpath-functions" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="3.0" expand-text="yes">
   <xsl:strip-space elements="*" />
   <xsl:output method="text" indent="no" />
   <xsl:param name="input" />
   <xsl:variable name="inputXml" select="json-to-xml($input)" />
   <xsl:template name="xsl:initial-template" match="/">
      <xsl:variable name="result">
         <!-- <xsl:apply-templates select="($inputXml)/j:map/j:array/*" /> -->
         <xsl:apply-templates select="($inputXml)/*" />
      </xsl:variable>
      {xml-to-json($result,map{'indent':true()})}
      <!-- <xsl:value-of select="$result" /> -->
   </xsl:template>


   <xsl:template match="j:map">
      <j:array key="tags">
           <xsl:apply-templates select="j:array[@key='item'][1]" mode="temp"/>
      </j:array>
   </xsl:template>


   <xsl:template match="j:map/*" mode="temp">
   <xsl:for-each select="j:map">
      <j:map>
         <!-- <j:string key="key">{j:string[@key='name']}</j:string> -->
         <xsl:variable name="inputTag" select="j:string[@key='name']" />
         <j:string key="name">{j:string[@key='name']}</j:string>
         <j:array key="paths">
            <xsl:apply-templates select="j:array" mode="group">
               <xsl:with-param name="tag" select="$inputTag" />
            </xsl:apply-templates>
         </j:array>
      </j:map>
   </xsl:for-each>
   </xsl:template>

   <xsl:template match="j:map/*" mode="group">
      <xsl:param name="tag" />
      <xsl:for-each select="j:map">
         <xsl:if test="not(exists(j:array[@key='item']/@key))">
            <j:map>
                  <!-- <j:string key="exists">{j:array[@key='item']/@key}</j:string> -->
                  <j:string key="inputParam">{$tag}</j:string>
                  <j:string key="key">{j:string[@key='name']}</j:string>
            </j:map>
         </xsl:if>
     </xsl:for-each>
   </xsl:template>



</xsl:stylesheet>
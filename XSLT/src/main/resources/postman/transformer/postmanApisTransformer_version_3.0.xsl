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
         <xsl:apply-templates select="j:array[@key='item'][1]" mode="temp" />
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
         <xsl:choose>
            <xsl:when test="not(exists(j:array[@key='item']/@key))">
               <j:map>
                  <!-- <j:string key="exists">{j:array[@key='item']/@key}</j:string> -->
                  <!-- <j:string key="inputParam">{$tag}</j:string> -->
                  <j:string key="summary">{j:string[@key='name']}</j:string>
                  <!-- Construct URL from path -->
                  <!-- <j:string key="url">{j:map[@key='request']/j:map[@key='url']/j:array[@key='path']}</j:string> -->

                  <!-- Building Url -->
                  <j:string key="url">
                     <xsl:for-each select="j:map[@key='request']/j:map[@key='url']/j:array[@key='path']/j:string">
                        <xsl:value-of select="." />
                        <xsl:if test="position() != last()">
                           <xsl:text>/</xsl:text>
                        </xsl:if>
                     </xsl:for-each>
                     <xsl:if test="exists(j:map[@key='request']/j:map[@key='url']/j:array[@key='query'])">
                        <xsl:text>?</xsl:text>
                        <xsl:for-each select="j:map[@key='request']/j:map[@key='url']/j:array[@key='query']/j:map">
                           <xsl:if test="position() != 1">
                              <xsl:text>&amp;</xsl:text>
                           </xsl:if>
                           <xsl:value-of select="j:string[@key='key']" />
                           <xsl:text>=</xsl:text>
                           <xsl:value-of select="j:string[@key='value']" />
                        </xsl:for-each>
                     </xsl:if>
                  </j:string>
                  <!-- Group Methods based on url (Pending) -->
                  <j:array key="methods">
                     <j:string>{upper-case(j:map[@key='request']/j:string[@key='method'])}</j:string>
                  </j:array>
               </j:map>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="exists(j:array[@key='item']/@key)">
                  <!-- <xsl:apply-templates select="j:array" mode="nestedGroup"/> -->
                  <xsl:apply-templates select="j:array" mode="group" />
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:for-each>
   </xsl:template>

   <!-- Not in Use-->
   <xsl:template match="j:map/*" mode="nestedGroup">
      <xsl:if test="@key='item'">
         <xsl:apply-templates select="j:map" mode="group" />
         <!-- <j:map>
         <j:string key="condition">{j:array/@key}</j:string>
         <j:string key="nested key">{@key}</j:string>
         <j:string key="summary">{j:map/j:string[@key='name']}</j:string>
      </j:map> -->
      </xsl:if>
      <!-- <xsl:choose>
      <xsl:when test="@key='item'">
         <j:map>
            <j:string key="temp">{@key}</j:string>
            <xsl:apply-templates select="../j:array" mode="nestedGroup" />
         </j:map>
      </xsl:when>
      <xsl:otherwise>
         <j:string key="nested key">{@key}</j:string>
         <j:string key="summary">{j:map/j:string[@key='name']}</j:string>
      </xsl:otherwise>
   </xsl:choose> -->
   </xsl:template>
</xsl:stylesheet>

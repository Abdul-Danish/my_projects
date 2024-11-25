<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:j="http://www.w3.org/2005/xpath-functions" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="3.0" expand-text="yes">
   <xsl:strip-space elements="*" />
   <xsl:output method="text" indent="no" />
   <xsl:param name="input" />
   <xsl:variable name="inputXml" select="json-to-xml($input)" />
   <xsl:template name="xsl:initial-template" match="/">
      <xsl:variable name="result">
         <xsl:apply-templates select="($inputXml)/*" />
      </xsl:variable>
      {xml-to-json($result,map{'indent':true()})}
   </xsl:template>
   <xsl:template match="j:map">
      <j:array key="tags">
         <xsl:apply-templates select="j:map[@key='paths'][1]" mode="temp" />
      </j:array>
   </xsl:template>
   <xsl:template match="j:map" mode="temp">
      <xsl:for-each select="../j:array[@key='tags']/j:map">
         <j:map>
            <xsl:variable name="inputTag" select="j:string[@key='name']" />
            <j:string key="name">{$inputTag}</j:string>
            <j:string key="value">{$inputTag}</j:string>
            <j:array key="paths">
               <xsl:apply-templates select="ancestor::j:map" mode="group">
                  <xsl:with-param name="tag" select="$inputTag" />
               </xsl:apply-templates>
            </j:array>
         </j:map>
      </xsl:for-each>
   </xsl:template>
   <xsl:template match="j:map" mode="group">
      <xsl:param name="tag" />
      <xsl:for-each select="j:map[@key='paths']/*">
         <xsl:if test="j:map[1]/j:array[@key='tags']=$tag ">
            <j:map>
               <j:string key="value">{@key}</j:string>
               <j:string key="name">{j:map[1]/j:string[@key='summary']}</j:string>
               <j:array key="methods">
                  <xsl:for-each select="j:map">
                     <j:map>
                        <j:string key="name">{upper-case(@key)}</j:string>
                        <j:string key="value">{upper-case(@key)}</j:string>
                        <j:array key="queryParams">
                           <xsl:if test="j:array[@key='parameters']/*">
                              <xsl:for-each select="j:array[@key='parameters']/j:map">
                                 <xsl:choose>
                                    <xsl:when test="j:string[@key='$ref']">
                                          <xsl:variable name="splitString" select="tokenize(j:string, '/')" />
                                          <!-- <xsl:for-each select="$splitString">
                                             <j:string key="{position()}">
                                                <xsl:value-of select="." />   
                                             </j:string>
                                          </xsl:for-each> -->
                                          <xsl:variable name="comp" select="$splitString[2]" />
                                          <xsl:variable name="param" select="$splitString[3]" />
                                          <xsl:variable name="qry" select="$splitString[4]" />

                                          <!-- <j:string key="condition">{../../../../../j:map/@key}</j:string> -->
                                          <xsl:for-each select="../../../../../j:map">
                                             <xsl:variable name="jsonKey" select="@key" />
                                             <xsl:if test="$jsonKey=$comp">
                                                <!-- <j:string key="del_me">{j:map[@key=$param]/j:map[@key=$qry]/j:string[@key='name']}</j:string> -->
                                                <xsl:if test="j:map[@key=$param]/*">
                                                   <xsl:for-each select="j:map[@key=$param]">
                                                      <xsl:if test="j:map[@key=$qry]/j:string[@key='in']='query'">
                                                      <j:map>
                                                         <j:string key="param">{j:map[@key=$qry]/j:string[@key='name']}</j:string>
                                                      </j:map>
                                                      </xsl:if>
                                                   </xsl:for-each>
                                                </xsl:if>
                                             </xsl:if>
                                          </xsl:for-each>

                                          <!-- <xsl:if test="../j:array[@key=$comp]">
                                             <j:string key="test">{../../../j:array[@key=$comp]/j:map[@key=$param]/j:map[@key=$qry]}</j:string>
                                          </xsl:if> -->

                                          <!-- <j:string key="ref">{j:string}</j:string> -->
                                    </xsl:when>
                                    <xsl:when test="j:string[@key='in']='query'">
                                       <j:map>
                                          <j:string key="param">{j:string[@key='name']}</j:string>
                                       </j:map>
                                    </xsl:when>
                                 </xsl:choose>
                              </xsl:for-each>
                           </xsl:if>
                        </j:array>
                     </j:map>
                  </xsl:for-each>
               </j:array>
            </j:map>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>
</xsl:stylesheet>

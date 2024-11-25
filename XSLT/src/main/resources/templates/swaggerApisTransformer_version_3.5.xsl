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
      {xml-to-json($result)}
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
                                    <!-- Parameter as reference -->
                                    <xsl:when test="j:string[@key='$ref']">
                                       <xsl:variable name="splitString" select="tokenize(j:string[@key='$ref'], '/')" />
                                       <xsl:variable name="components" select="$splitString[2]" />
                                       <xsl:variable name="parameters" select="$splitString[3]" />
                                       <xsl:variable name="paramName" select="$splitString[4]" />
                                       <xsl:for-each select="ancestor::j:map/j:map[@key=$components]">
                                          <xsl:if test="j:map[@key=$parameters]/j:map[@key=$paramName]/* and j:map[@key=$parameters]/j:map[@key=$paramName]/j:string[@key='in']='query'">
                                             <j:map>
                                                <j:string key="param">{j:map[@key=$parameters]/j:map[@key=$paramName]/j:string[@key='name']}</j:string>
                                                <xsl:choose>
                                                   <xsl:when test="number(j:map[@key=$parameters]/j:map[@key=$paramName]/j:map[@key='schema']/j:number[@key='default'])">
                                                      <j:string key="value">{j:map[@key=$parameters]/j:map[@key=$paramName]/j:map[@key='schema']/j:number[@key='default']}</j:string>
                                                   </xsl:when>
                                                   <xsl:when test="boolean(j:map[@key=$parameters]/j:map[@key=$paramName]/j:map[@key='schema']/j:boolean[@key='default'])">
                                                      <j:string key="value">{j:map[@key=$parameters]/j:map[@key=$paramName]/j:map[@key='schema']/j:boolean[@key='default']}</j:string>
                                                   </xsl:when>
                                                   <xsl:otherwise>
                                                      <j:string key="value">{j:map[@key=$parameters]/j:map[@key=$paramName]/j:map[@key='schema']/j:string[@key='default']}</j:string>
                                                   </xsl:otherwise>
                                                </xsl:choose>
                                             </j:map>
                                          </xsl:if>
                                       </xsl:for-each>
                                    </xsl:when>
                                    <!-- Parameter as inline -->
                                    <xsl:when test="j:string[@key='in']='query'">
                                       <j:map>
                                          <j:string key="param">{j:string[@key='name']}</j:string>
                                          <xsl:choose>
                                             <xsl:when test="number(j:map[@key='schema']/j:number[@key='default'])">
                                                <j:string key="value">{j:map[@key='schema']/j:number[@key='default']}</j:string>
                                             </xsl:when>
                                             <xsl:when test="boolean(j:map[@key='schema']/j:boolean[@key='default'])">
                                                <j:string key="value">{j:map[@key='schema']/j:boolean[@key='default']}</j:string>
                                             </xsl:when>
                                             <xsl:otherwise>
                                                <j:string key="value">{j:map[@key='schema']/j:string[@key='default']}</j:string>
                                             </xsl:otherwise>
                                          </xsl:choose>
                                       </j:map>
                                    </xsl:when>
                                 </xsl:choose>
                              </xsl:for-each>
                           </xsl:if>
                        </j:array>
                        <j:array key="consumeParams">
                           <xsl:for-each select="j:array[@key='consumes']/j:string">
                              <j:map>
                                 <j:string key="param">Content-Type</j:string>
                                 <j:string key="value">{.}</j:string>
                              </j:map>
                           </xsl:for-each>
                        </j:array>
                        <j:array key="produceParams">
                           <xsl:for-each select="j:array[@key='produces']/j:string">
                              <j:map>
                                 <j:string key="param">Accept</j:string>
                                 <j:string key="value">{.}</j:string>
                              </j:map>
                           </xsl:for-each>
                        </j:array>
                     </j:map>
                  </xsl:for-each>
               </j:array>
            </j:map>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>
</xsl:stylesheet>

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
         <xsl:apply-templates select="j:array[@key='item'][1]" mode="root-collection" />
         <xsl:apply-templates select="j:array[@key='item'][1]" mode="process" />
      </j:array>
   </xsl:template>
   <!-- For root collection -->
   <xsl:template match="j:map/*" mode="root-collection">
      <xsl:if test="j:map/j:map[@key='request']">
         <j:map>
            <j:string key="name">default</j:string>
            <j:string key="value">default</j:string>
            <j:array key="paths">
               <xsl:apply-templates select="j:map" mode="root-collection-group" />
            </j:array>
         </j:map>
      </xsl:if>
   </xsl:template>
   <xsl:template match="j:map" mode="root-collection-group">
      <xsl:if test="not(j:array[@key='item']/@key) and j:map[@key='request']/* and (j:map[@key='request']/j:string[@key='url'] or j:map[@key='request']/j:map[@key='url'])">
         <j:map>
            <j:string key="name">{j:string[@key='name']}</j:string>
            <!-- URL -->
            <xsl:variable name="rawUrl">
               <xsl:choose>
                  <xsl:when test="j:map[@key='request']/j:map[@key='url']/j:string[@key='raw']">
                     <xsl:value-of select="j:map[@key='request']/j:map[@key='url']/j:string[@key='raw']" />
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="j:map[@key='request']/j:string[@key='url']" />
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <xsl:variable name="url" select="tokenize($rawUrl, '\?')" />
            <j:string key="value">{$url[1]}</j:string>
            <!-- Methods -->
            <j:array key="methods">
               <xsl:choose>
                  <xsl:when test="j:map[@key='request']/j:array[@key='method']">
                     <xsl:for-each select="j:map[@key='request']/j:array[@key='method']/j:string">
                        <j:map>
                           <xsl:variable name="currentMethod" select="upper-case(.)" />
                           <j:string key="name">{$currentMethod}</j:string>
                           <j:string key="value">{$currentMethod}</j:string>
                           <xsl:choose>
                              <xsl:when test="../../../j:map[@key='request']/j:map[@key='queryParams']/j:array/*">
                                 <j:array key="queryParams">
                                    <xsl:for-each select="../../../j:map[@key='request']/j:map[@key='queryParams']/j:array">
                                       <xsl:if test="@key=$currentMethod">
                                          <xsl:for-each select="j:string">
                                             <xsl:variable name="currentQueryParam" select="." />
                                             <xsl:variable name="queryParam" select="tokenize($currentQueryParam, '=')" />
                                             <j:map>
                                                <j:string key="param">{$queryParam[1]}</j:string>
                                                <j:string key="value">{$queryParam[2]}</j:string>
                                             </j:map>
                                          </xsl:for-each>
                                       </xsl:if>
                                    </xsl:for-each>
                                 </j:array>
                              </xsl:when>
                              <xsl:otherwise>
                                 <j:array key="queryParams">
                                    <xsl:variable name="concatenatedQuery" select="tokenize($rawUrl, '\?')" />
                                    <xsl:variable name="splitParameters" select="tokenize($concatenatedQuery[2], '&amp;')" />
                                    <xsl:for-each select="$splitParameters">
                                       <xsl:variable name="currentQueryParam" select="." />
                                       <xsl:variable name="queryParam" select="tokenize($currentQueryParam, '=')" />
                                       <j:map>
                                          <j:string key="param">{$queryParam[1]}</j:string>
                                          <j:string key="value">{$queryParam[2]}</j:string>
                                       </j:map>
                                    </xsl:for-each>
                                 </j:array>
                              </xsl:otherwise>
                           </xsl:choose>
                           <j:array key="headerParams">
                              <xsl:if test="../../../j:map[@key='request']/j:array[@key='header']/j:map/j:array/*">
                                 <xsl:for-each select="../../../j:map[@key='request']/j:array[@key='header']/j:map/j:array">
                                    <xsl:if test="upper-case(@key)=$currentMethod">
                                       <xsl:for-each select="j:map">
                                          <j:map>
                                             <j:string key="param">{j:string[@key='key']}</j:string>
                                             <j:string key="value">{j:string[@key='value']}</j:string>
                                          </j:map>
                                       </xsl:for-each>
                                    </xsl:if>
                                 </xsl:for-each>
                              </xsl:if>
                           </j:array>
                        </j:map>
                     </xsl:for-each>
                  </xsl:when>
                  <xsl:otherwise>
                     <j:map>
                        <j:string key="name">{upper-case(j:map[@key='request']/j:string[@key='method'])}</j:string>
                        <j:string key="value">{upper-case(j:map[@key='request']/j:string[@key='method'])}</j:string>
                        <j:array key="queryParams">
                           <xsl:variable name="concatenatedQuery" select="tokenize($rawUrl, '\?')" />
                           <xsl:variable name="splitParameters" select="tokenize($concatenatedQuery[2], '&amp;')" />
                           <xsl:for-each select="$splitParameters">
                              <xsl:variable name="currentQueryParam" select="." />
                              <xsl:variable name="queryParam" select="tokenize($currentQueryParam, '=')" />
                              <j:map>
                                 <j:string key="param">{$queryParam[1]}</j:string>
                                 <j:string key="value">{$queryParam[2]}</j:string>
                              </j:map>
                           </xsl:for-each>
                        </j:array>
                        <j:array key="headerParams">
                           <xsl:if test="j:map[@key='request']/j:array[@key='header']/j:map">
                              <xsl:for-each select="j:map[@key='request']/j:array[@key='header']/j:map">
                                 <j:map>
                                    <j:string key="param">{j:string[@key='key']}</j:string>
                                    <j:string key="value">{j:string[@key='value']}</j:string>
                                 </j:map>
                              </xsl:for-each>
                           </xsl:if>
                        </j:array>
                     </j:map>
                  </xsl:otherwise>
               </xsl:choose>
            </j:array>
         </j:map>
      </xsl:if>
   </xsl:template>
   <!-- For non-root collection -->
   <xsl:template match="j:map/*" mode="process">
      <xsl:for-each select="j:map">
         <xsl:if test="exists(j:array[@key='item'])">
            <j:map>
               <xsl:variable name="inputTag" select="j:string[@key='name']" />
               <j:string key="name">{j:string[@key='name']}</j:string>
               <j:string key="value">{j:string[@key='name']}</j:string>
               <j:array key="paths">
                  <xsl:apply-templates select="j:array" mode="group">
                     <xsl:with-param name="tag" select="$inputTag" />
                  </xsl:apply-templates>
               </j:array>
            </j:map>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>
   <xsl:template match="j:map/*" mode="group">
      <xsl:param name="tag" />
      <xsl:for-each select="j:map">
         <xsl:choose>
            <xsl:when test="not(exists(j:array[@key='item']/@key)) and j:map[@key='request']/* and (j:map[@key='request']/j:string[@key='url'] or j:map[@key='request']/j:map[@key='url'])">
               <j:map>
                  <!-- Desc -->
                  <j:string key="name">{j:string[@key='name']}</j:string>
                  <!-- URL -->
                  <xsl:variable name="rawUrl">
                     <xsl:choose>
                        <xsl:when test="j:map[@key='request']/j:map[@key='url']/j:string[@key='raw']">
                           <xsl:value-of select="j:map[@key='request']/j:map[@key='url']/j:string[@key='raw']" />
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:value-of select="j:map[@key='request']/j:string[@key='url']" />
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:variable>
                  <xsl:variable name="url" select="tokenize($rawUrl, '\?')" />
                  <j:string key="value">{$url[1]}</j:string>
                  <!-- Methods -->
                  <j:array key="methods">
                     <xsl:choose>
                        <xsl:when test="j:map[@key='request']/j:array[@key='method']">
                           <xsl:for-each select="j:map[@key='request']/j:array[@key='method']/j:string">
                              <j:map>
                                 <xsl:variable name="currentMethod" select="upper-case(.)" />
                                 <j:string key="name">{$currentMethod}</j:string>
                                 <j:string key="value">{$currentMethod}</j:string>
                                 <xsl:choose>
                                    <xsl:when test="../../../j:map[@key='request']/j:map[@key='queryParams']/j:array/*">
                                       <j:array key="queryParams">
                                          <xsl:for-each select="../../../j:map[@key='request']/j:map[@key='queryParams']/j:array">
                                             <xsl:if test="@key=$currentMethod">
                                                <xsl:for-each select="j:string">
                                                   <xsl:variable name="currentQueryParam" select="." />
                                                   <xsl:variable name="queryParam" select="tokenize($currentQueryParam, '=')" />
                                                   <j:map>
                                                      <j:string key="param">{$queryParam[1]}</j:string>
                                                      <j:string key="value">{$queryParam[2]}</j:string>
                                                   </j:map>
                                                </xsl:for-each>
                                             </xsl:if>
                                          </xsl:for-each>
                                       </j:array>
                                    </xsl:when>
                                    <xsl:otherwise>
                                       <j:array key="queryParams">
                                          <xsl:variable name="concatenatedQuery" select="tokenize($rawUrl, '\?')" />
                                          <xsl:variable name="splitParameters" select="tokenize($concatenatedQuery[2], '&amp;')" />
                                          <xsl:for-each select="$splitParameters">
                                             <xsl:variable name="currentQueryParam" select="." />
                                             <xsl:variable name="queryParam" select="tokenize($currentQueryParam, '=')" />
                                             <j:map>
                                                <j:string key="param">{$queryParam[1]}</j:string>
                                                <j:string key="value">{$queryParam[2]}</j:string>
                                             </j:map>
                                          </xsl:for-each>
                                       </j:array>
                                    </xsl:otherwise>
                                 </xsl:choose>
                                 <j:array key="headerParams">
                                    <xsl:if test="../../../j:map[@key='request']/j:array[@key='header']/j:map/j:array/*">
                                       <xsl:for-each select="../../../j:map[@key='request']/j:array[@key='header']/j:map/j:array">
                                          <xsl:if test="@key=$currentMethod">
                                             <xsl:for-each select="j:map">
                                                <j:map>
                                                   <j:string key="param">{j:string[@key='key']}</j:string>
                                                   <j:string key="value">{j:string[@key='value']}</j:string>
                                                </j:map>
                                             </xsl:for-each>
                                          </xsl:if>
                                       </xsl:for-each>
                                    </xsl:if>
                                 </j:array>
                              </j:map>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <j:map>
                              <j:string key="name">{upper-case(j:map[@key='request']/j:string[@key='method'])}</j:string>
                              <j:string key="value">{upper-case(j:map[@key='request']/j:string[@key='method'])}</j:string>
                              <j:array key="queryParams">
                                 <xsl:variable name="concatenatedQuery" select="tokenize($rawUrl, '\?')" />
                                 <xsl:variable name="splitParameters" select="tokenize($concatenatedQuery[2], '&amp;')" />
                                 <xsl:for-each select="$splitParameters">
                                    <xsl:variable name="currentQueryParam" select="." />
                                    <xsl:variable name="queryParam" select="tokenize($currentQueryParam, '=')" />
                                    <j:map>
                                       <j:string key="param">{$queryParam[1]}</j:string>
                                       <j:string key="value">{$queryParam[2]}</j:string>
                                    </j:map>
                                 </xsl:for-each>
                              </j:array>
                              <j:array key="headerParams">
                                 <xsl:if test="j:map[@key='request']/j:array[@key='header']/j:map">
                                    <xsl:for-each select="j:map[@key='request']/j:array[@key='header']/j:map">
                                       <j:map>
                                          <j:string key="param">{j:string[@key='key']}</j:string>
                                          <j:string key="value">{j:string[@key='value']}</j:string>
                                       </j:map>
                                    </xsl:for-each>
                                 </xsl:if>
                              </j:array>
                           </j:map>
                        </xsl:otherwise>
                     </xsl:choose>
                  </j:array>
               </j:map>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="exists(j:array[@key='item']/@key)">
                  <xsl:apply-templates select="j:array" mode="group" />
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:for-each>
   </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:j="http://www.w3.org/2005/xpath-functions" version="3.0" expand-text="yes">

    <xsl:output method="text" indent="yes" />
    <xsl:param name="input" />
    <xsl:variable name="inputXml" select="json-to-xml($input)" />

    <xsl:template name="xsl:initial-template" match="/">

        <xsl:variable name="result">
            <xsl:apply-templates select="($inputXml)/*"/>
        </xsl:variable>

        {xml-to-json($result,map{'indent':true()})}

                <!-- <xsl:value-of select="$result" /> -->
        <!-- <xsl:for-each-group select="j:map" group-by="j:tag">
            <xsl:if test="j:map[@key='paths']">
                <j:map>
                    <xsl:text>tag: </xsl:text>
                    <xsl:value-of select="j:map/map/array[@key='tags']" />
                </j:map>
            </xsl:if>
        </xsl:for-each-group> -->
    </xsl:template>

    <xsl:template match="j:map">
        <!-- <j:array> -->
        <j:map>
            <!-- <xsl:apply-templates select="j:map[@key='paths']/j:map[1]/j:array[@key='tags']" mode="temp" /> -->
            <!-- <j:map> -->
            <j:array key="tags">
                <xsl:apply-templates select="j:map[@key='paths'][1]" mode="temp" />
                <!-- <xsl:apply-templates select="j:map[1]" mode="temp" /> -->
            </j:array>
            <!-- </j:map> -->
        </j:map>
        <!-- </j:array> -->
    </xsl:template>

    <!-- <xsl:template match="j:map/*">
        <xsl:variable name="tst" select="{@key}" /> -->
        <!-- <xsl:text>[</xsl:text> -->
        <!-- <xsl:for-each select="j:map">
            <j:string key="methods">{@key}</j:string>
            <xsl:choose>
                <xsl:when test="position() != last()">
                    <xsl:text>,</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>]</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:text>&#xa;</xsl:text>
        <xsl:text>&#xa;</xsl:text> -->
    <!-- </xsl:template> -->

    <!-- <xsl:template select="j:map/" mode="group">
    </xsl:template> -->

    <xsl:template match="j:map" mode="temp">
        <xsl:for-each select="../j:array[@key='tags']/j:map">
            <j:map>
            <!-- <j:string key="tgs">{j:string[@key='name']}</j:string> -->
            
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

    <!-- <xsl:template match="j:map/j:map[@key='paths']/*" mode="group"> -->
    <xsl:template match="j:map" mode="group">

        <xsl:param name="tag" />
        <xsl:for-each select="j:map[@key='paths']/*">
        <!-- <j:map> -->
        <!-- <j:array key="{$tag}"> -->
            <!-- <j:string key="param">{$tag}</j:string> -->
            <!-- <j:string key="key">{@key}</j:string> -->
            <!-- <j:string key="tmpPath">{j:map[@key='paths'][1]/j:map/@key}</j:string> -->
            <!-- <j:string key="tmpTag">{j:map[1]/j:array[@key='tags']}</j:string> -->
            <!-- <j:string key="condition">{j:map[1]/j:array[@key='tags']}</j:string> -->

            <xsl:if test="j:map[1]/j:array[@key='tags']=$tag ">
                <j:map>
                    <!-- <j:string key="param">{$tag}</j:string>
                    <j:string key="tag">{j:map[1]/j:array[@key='tags']}</j:string> -->
                    <j:string key="value">{@key}</j:string>
                    <j:string key="name">{j:map[1]/j:string[@key='summary']}</j:string>
                    <j:string key="methods">
                    <xsl:text>[</xsl:text>
                    <xsl:for-each select="j:map">
                        <xsl:text>{@key}</xsl:text>
                        <xsl:choose>
                            <xsl:when test="position() != last()">
                                <xsl:text>,</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>]</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                    </j:string>

                    <!-- <j:string key="methods">
                        <xsl:for-each select="j:map">
                            <j:string key="{@key}"></j:string>
                            <xsl:text>{@key}</xsl:text>
                        </xsl:for-each>
                    </j:string> -->

                </j:map>
            </xsl:if>
        <!-- </j:array> -->
        <!-- </j:map> -->

        </xsl:for-each>
    </xsl:template>

    <!-- <xsl:template match="j:map/j:map[@key='paths']/*" mode="temp"> -->
        
        <!-- <xsl:for-each select="/j:map[@key='paths']"> -->
        <!-- <j:array key="tag"> -->
        <!-- <j:string key="tags">{j:map[1]/j:array[@key='tags']}</j:string> -->
        
        <!-- <xsl:for-each select="../@key">
        <j:map>
            <j:string key="tg">{@key}</j:string>
        </j:map>
        </xsl:for-each> -->
        
            <!-- <j:map> -->
            <!-- <xsl:value-of select="@key" /> -->
            <!-- <xsl:text>path: </xsl:text> -->
            <!-- <j:string key="path">{j:map}</j:string> -->
            <!-- <xsl:text>&#xa;</xsl:text> -->

            <!-- For tag -->
            <!-- <j:string key="tags">{j:map[1]/j:array[@key='tags']}</j:string> -->
            <!-- <xsl:text>&#xa;</xsl:text> -->

            <!-- For summary -->
            <!-- <j:string key="summary">{j:map[1]/j:string[@key='summary']}</j:string> -->
            <!-- <xsl:text>&#xa;</xsl:text> -->


            <!-- <xsl:text>methods: </xsl:text> -->
            <!-- <xsl:text>[</xsl:text>
                        <xsl:for-each select="j:map">
                            <j:string key="methods">{@key}</j:string>
                            <xsl:choose>
                                <xsl:when test="position() != last()">
                                    <xsl:text>,</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>]</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                        <xsl:text>&#xa;</xsl:text>
                        <xsl:text>&#xa;</xsl:text> -->
            <!-- </j:map> -->
            <!-- </xsl:for-each-group> -->
            <!-- </j:array> -->
        <!-- </j:map> -->

        <!-- </j:array> -->
        <!-- </xsl:for-each> -->
    <!-- </xsl:template> -->


</xsl:stylesheet>
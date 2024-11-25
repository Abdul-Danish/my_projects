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
        
        <!-- {xml-to-json($result,map{'indent':true()})} -->
        <xsl:value-of select="$result" />
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
        <!-- <j:map> -->
        <!-- <j:string key="tags"><j:string> -->
        <j:map >
            <!-- <j:array key="pts"> -->
                <xsl:apply-templates select="j:map[@key='paths']" mode="path" />
            <!-- </j:array> -->
        </j:map>
        <!-- </j:map> -->
    </xsl:template>

    <xsl:template match="j:map/j:map[@key='paths']/*" mode="path">
        <!-- <xsl:for-each select="/j:map[@key='paths']"> -->
            <!-- <j:map> -->
                <!-- <xsl:value-of select="@key" /> -->
                <xsl:text>path: </xsl:text>
                <j:string key="path">{@key}</j:string>
                <xsl:text>&#xa;</xsl:text>

                <xsl:for-each select="j:map">
                    <xsl:if test="position() = 1">
                        <!-- For tag -->
                        <xsl:text>tag: </xsl:text>
                        <xsl:value-of select="j:array[@key='tags']" />
                        <!-- <j:string key="tags">{j:array[@key='tags']}</j:string> -->
                        <xsl:text>&#xa;</xsl:text>

                        <!-- For summary -->
                        <xsl:text>summary: </xsl:text>
                        <xsl:value-of select="j:string[@key='summary']" />
                        <!-- <j:string key="summary">{j:map/j:string[@key='summary']}</j:string> -->
                        <xsl:text>&#xa;</xsl:text>
                    </xsl:if>
                </xsl:for-each>
                

                <xsl:text>methods: </xsl:text>
                <xsl:text>[</xsl:text>
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
                <xsl:text>&#xa;</xsl:text>
            <!-- </j:map> -->
        <!-- </xsl:for-each> -->
    </xsl:template>


</xsl:stylesheet>
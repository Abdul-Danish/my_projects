<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:j="http://www.w3.org/2005/xpath-functions" version="3.0" expand-text="yes">
	<xsl:strip-space elements="*"/>
    <xsl:output method="text" indent="no" />

    <xsl:param name="input" />
    <xsl:variable name="inputXml" select="json-to-xml($input)" />

    <xsl:template name="xsl:initial-template" match="/">

        <xsl:variable name="result">
            <xsl:apply-templates select="($inputXml)/*"/>
        </xsl:variable>

        {xml-to-json($result,map{'indent':true()})}
    </xsl:template>

    <xsl:template match="j:map">
            <j:array key="tags">
                <xsl:apply-templates select="j:map[@key='paths'][1]" mode="default" />
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
                            <j:string>{upper-case(@key)}</j:string>
                        </xsl:for-each>
                    </j:array>

                    <!-- <j:string key="methods">
                        <xsl:text>[</xsl:text>
                        <xsl:for-each select="j:map">
                            <xsl:text>{upper-case(@key)}</xsl:text>
                            <xsl:choose>
                                <xsl:when test="position() != last()">
                                    <xsl:text>,</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>]</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </j:string> -->
                </j:map>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="j:map" mode="default">
        <!-- <xsl:for-each select="../j:array[@key='tags']/j:map"> -->
            <j:map>
                <!-- <xsl:variable name="inputTag" select="j:string[@key='name']" /> -->
                <j:string key="name">default</j:string>
                <j:string key="value">default</j:string>
                <j:array key="paths">
                    <xsl:apply-templates select="ancestor::j:map" mode="emptyTags" />
                        <!-- <xsl:with-param name="tag" select="$inputTag" /> -->
                    <!-- </xsl:apply-templates> -->
                </j:array>
            </j:map>
        <!-- </xsl:for-each> -->
    </xsl:template>

    <xsl:template match="j:map" mode="emptyTags">

        <!-- <xsl:param name="tag" /> -->
        <xsl:for-each select="j:map[@key='paths'][1]/j:map">
            <!-- <j:map>
            <j:string key="tst">{j:map[1]/@key}</j:string>
            </j:map> -->
            <xsl:if test="j:map[1]/j:array[@key='tags']='' or not(exists(j:map[1]/j:array[@key='tags']))">
                <j:map>
                    <j:string key="value">{@key}</j:string>
                    <j:string key="name">{j:map[1]/j:string[@key='summary']}</j:string>

                    <j:array key="methods">
                        <xsl:for-each select="j:map">
                            <j:string>{upper-case(@key)}</j:string>
                        </xsl:for-each>
                    </j:array>
                </j:map>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>


</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:j="http://www.w3.org/2005/xpath-functions" version="3.0"
    xmlns:array="http://www.w3.org/2005/xpath-functions/array" xpath-default-namespace="http://www.w3.org/2005/xpath-functions">
    <xsl:output method="text" indent="yes"/>

    <xsl:param name="input"/>
    <xsl:variable name="employees-a" select="json-to-xml($input)"/>
    <xsl:template name="xsl:initial-template" match="/">
        <xsl:variable name="persons-b">
            <xsl:apply-templates select="$employees-a/*"/>
        </xsl:variable>
        {xml-to-json($persons-b,map{'indent':true()})}
    </xsl:template>
    <xsl:template match="j:map">
        <j:map>
            <j:array key="persons">
                <xsl:apply-templates select="j:map[@key='employees']/j:map" mode="employee"/>
            </j:array>
        </j:map>
    </xsl:template>
    <xsl:template match="j:map" mode="employee">
        <j:map>
            <!-- <j:string key="id">{@key}</j:string> -->
            <j:string key="fullName">{j:string[@key='firstname']||' '||j:string [@key='surname']}</j:string>
            <!-- <j:string key="reverseName">{j:string [@key='surname']||', '||j:string [@key='firstname']}</j:string>
            <xsl:copy-of select="*[@key=('firstname','surname','department')]"/>
            <xsl:if test="j:string [@key='manager']">
                <j:string key="reportsTo">{j:string [@key='manager']/text()}</j:string>
            </xsl:if> -->
        </j:map>
    </xsl:template>
</xsl:stylesheet>
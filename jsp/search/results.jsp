<%--
  - results.jsp
  -
  - Version: $Revision$
  -
  - Date: $Date$
  -
  - Copyright (c) 2001, Hewlett-Packard Company and Massachusetts
  - Institute of Technology.  All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are
  - met:
  -
  - - Redistributions of source code must retain the above copyright
  - notice, this list of conditions and the following disclaimer.
  -
  - - Redistributions in binary form must reproduce the above copyright
  - notice, this list of conditions and the following disclaimer in the
  - documentation and/or other materials provided with the distribution.
  -
  - - Neither the name of the Hewlett-Packard Company nor the name of the
  - Massachusetts Institute of Technology nor the names of their
  - contributors may be used to endorse or promote products derived from
  - this software without specific prior written permission.
  -
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  - ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  - LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  - A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  - HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  - INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  - BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
  - OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  - ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
  - TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
  - USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
  - DAMAGE.
  --%>


<%--
  - Display the results of a simple search
  -
  - Attributes to pass in:
  -
  -   community        - pass in if the scope of the search was a community
  -                      or a collection in this community
  -   collection       - pass in if the scope of the search was a collection
  -   community.array  - if the scope of the search was "all of DSpace", pass
  -                      in all the communities in DSpace as an array to
  -                      display in a drop-down box
  -   collection.array - if the scope of a search was a community, pass in an
  -                      array of the collections in the community to put in
  -                      the drop-down box
  -   items            - the results.  An array of Items, most relevant first
  -   communities
  -   collections
  -   item_handles     - Handles corresponding to the results. (String[])
  -   community_handles
  -   collection_handles
  -
  -   query            - The original query
  --%>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="org.dspace.content.Community" %>
<%@ page import="org.dspace.content.Collection" %>
<%@ page import="org.dspace.content.Item" %>

<%
    // Get the attributes
    Community   community  = (Community ) request.getAttribute("community" );
    Collection  collection = (Collection) request.getAttribute("collection");
    Community[] communityArray = (Community[]) request.getAttribute("community.array");
    Collection[] collectionArray = (Collection[]) request.getAttribute("collection.array");
    
    Item      [] items       = (Item[]      )request.getAttribute("items");
    Community [] communities = (Community[] )request.getAttribute("communities");
    Collection[] collections = (Collection[])request.getAttribute("collections");
    
    String[] itemHandles       = (String[]) request.getAttribute("item_handles");
    String[] communityHandles  = (String[]) request.getAttribute("community_handles");
    String[] collectionHandles = (String[]) request.getAttribute("collection_handles");
    
    String query = (String) request.getAttribute("query");
%>

<dspace:layout title="Search Results">

    <H1>Search Results</H1>
    
    <%-- Controls for a repeat search --%>
    <form action="simple-search" method=GET>
        <table class=miscTable align=center>
            <tr>
                <td class="evenRowEvenCol">
                    <table>
                        <tr>
                            <td>
                                <strong>Search:</strong>&nbsp;<select name="location">
<%
    if (community == null && collection == null)
    {
        // Scope of the search was all of DSpace.  The scope control will list
        // "all of DSpace" and the communities.
%>
                                    <option selected value="/">All of DSpace</option>
<%
        for (int i = 0; i < communityArray.length; i++)
        {
%>
                                    <option value="/communities/<%= communityArray[i].getID() %>/"><%= communityArray[i].getMetadata("name") %></option>
<%
        }
    }
    else if (collection == null)
    {
        // Scope of the search was within a community.  Scope control will list
        // "all of DSpace", the community, and the collections within the community.
%>
                                    <option value="/">All of DSpace</option>
                                    <option selected value="/communities/<%= community.getID() %>/"><%= community.getMetadata("name") %></option>
<%
        for (int i = 0; i < collectionArray.length; i++)
        {
%>
                                    <option value="/communities/<%= community.getID() %>/collections/<%= collectionArray[i].getID() %>/"><%= collectionArray[i].getMetadata("name") %></option>
<%
        }
    }
    else
    {
        // Scope of the search is a specific collection
%>
                                    <option value="/">All of DSpace</option>
                                    <option value="/communities/<%= community.getID() %>/"><%= community.getMetadata("name") %></option>
                                    <option selected value="/communities/<%= community.getID() %>/collections/<%= collection.getID() %>/"><%= collection.getMetadata("name") %></option>
<%
    }
%>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td align=center>
                                for&nbsp;<input type="text" name="query" value="<%= (query==null ? "" : query) %>">&nbsp;<input type="submit" value="Go">
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </form>

<% if (communities.length > 0 ) { %>
    <dspace:communitylist  communities="<%= communities %>" handles="<%=
    communityHandles %>" />
<% } %>

<% if (collections.length > 0 ) { %>   
    <dspace:collectionlist collections="<%= collections %>" handles="<%=
    collectionHandles %>" />
<% } %>

    <P align=center>Found <%= items.length == 0 ? "no" : String.valueOf(items.length) %> item<%= items.length != 1 ? "s" : "" %>.</P>

    <dspace:itemlist items="<%= items %>" handles="<%= itemHandles %>" />
</dspace:layout>


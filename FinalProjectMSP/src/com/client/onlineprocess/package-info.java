/**
 * 
 */
/**
 * @author Preetham
 * Client side online process: Performs the following bunch of talks
 * a) Identifies the scene changes frames for the query video
 * b) Extract video content --> SIFT descriptors for query video
 * c) Extract video content ---> color histograms for query video
 * d) Extract audio content --> fingerprint similarity for query video
 * e) compare the results with stored metadata
 * f) Generate a graph using the compared results
 */
package com.client.onlineprocess;
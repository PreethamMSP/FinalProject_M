/**
 * 
 */
/**
 * @author Preetham
 * 
 * Server side offline process: Performs the following bunch of talks
 * a) Identifies the scene changes frames
 * b) Extract video content --> SIFT descriptors
 * c) Extract video content ---> color histograms
 * d) Extract audio content --> fingerprint similarity
 * e) Store the extracted metadata
 *
 */
package com.server.offlineprocess;
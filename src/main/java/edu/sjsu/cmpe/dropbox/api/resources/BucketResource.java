package edu.sjsu.cmpe.dropbox.api.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;


















import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.amazonaws.services.s3.model.CopyPartRequest;
import com.amazonaws.services.s3.model.CopyPartResult;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

//import com.amazonaws.regions.Regions;
//import com.amazonaws.regions.Region;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;

















import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.core.CacheControl;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.sjsu.cmpe.dropbox.config.dropboxServiceConfiguration;
import edu.sjsu.cmpe.dropbox.domain.AmazonCredentials;
import edu.sjsu.cmpe.dropbox.domain.NewFile;

//import javax.ws.rs.core.Response.ResponseBuilder;
//import javax.ws.rs.core.Request;

//import javax.ws.rs.core.UriInfo;




















//import com.sun.research.ws.wadl.Request;
import com.yammer.dropwizard.jersey.params.LongParam;
import com.yammer.metrics.annotation.Timed;
















//import edu.sjsu.cmpe.dropbox.domain.BucketDetails;
import edu.sjsu.cmpe.dropbox.dto.*;

import java.util.ArrayList;



@Path("/v1/files")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BucketResource {
	private MongoTest mongo;
	public BucketResource(MongoTest mongo){
		this.mongo = mongo;
	}
    @GET
    @Path("/old/{existinguser}/download")
    @Timed(name = "download-file")
       public Response downloadFile(@QueryParam("filepath") String filePath, @QueryParam("fileName") String fileName,
   			@PathParam("existinguser") String existingUser) throws IOException {
    	AmazonCredentials myCredentials = new AmazonCredentials();
		AWSCredentials credentials = myCredentials.getCredentials();
    	System.out.println("Inside upload-file");
    	
		//test
    	AmazonS3 s3Client = new AmazonS3Client(credentials);
    	
    	String key = fileName;
      	s3Client.setEndpoint("http://s3-us-west-1.amazonaws.com");
      	String bucketName = mongo.getBucketName(existingUser);
      	S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
        InputStream reader = new BufferedInputStream(object.getObjectContent());
      	File file = new File(filePath);      
      	OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

      		int read = -1;

      		while ( ( read = reader.read() ) != -1 ) {
      		    writer.write(read);
      		}

      		writer.flush();
      		writer.close();
      		reader.close();

        return Response.status(200).entity("All files displayed").build();
    	
    }
    
    @GET
	@Path("/old/{existinguser}/share/download")
	@Timed(name = "download-shared-file")
    public Response downloadSharedFile(@QueryParam("filePath") String filePath, @QueryParam("fileName") String fileName,
   			@PathParam("existinguser") String existingUser) throws IOException {
    	AmazonCredentials myCredentials = new AmazonCredentials();
		AWSCredentials credentials = myCredentials.getCredentials();
    	System.out.println("Inside download-share-file");
    	
		//test
    	AmazonS3 s3Client = new AmazonS3Client(credentials);
    	
    	String key = fileName;
    	  System.out.println("key : "+ key);
      	s3Client.setEndpoint("http://s3-us-west-1.amazonaws.com");
      	String bucketName = mongo.getBucketName(existingUser);
      	S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
        InputStream reader = new BufferedInputStream(object.getObjectContent());
        System.out.println("filepath : " + filePath);
      	File file = new File(filePath);      
      	OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

      		int read = -1;

      		while ( ( read = reader.read() ) != -1 ) {
      		    writer.write(read);
      		}

      		writer.flush();
      		writer.close();
      		reader.close();

        return Response.status(200).entity("File Downloaded").build();
    	
    }
    
    
    private String getFileNameFromPath(String filePath) {
		// TODO Auto-generated method stub
		String fileName=null;
		String newstr=filePath;
		System.out.println(filePath);
		String[] str=newstr.split("\\\\");
		for (String string : str) {
			fileName=string;
		//	System.out.println(string);
		}
		System.out.println("update for "+fileName);
		//compute the fileName
		return(fileName);
	}
    
    private long getFileSize(String filePath) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		long fileSize = file.length() / 1048786;
		System.out.println("File size is+"+fileSize);
		return fileSize;
	}

    @POST
    @Path("/old/{existinguser}/upload")
    @Timed(name = "upload-file")
       public Response uploadFile(@PathParam("existinguser") String existingUser, @QueryParam("filepath") String filePath) throws IOException{
    	AmazonCredentials myCredentials = new AmazonCredentials();
		AWSCredentials credentials = myCredentials.getCredentials();
    	System.out.println("Inside upload-file");
    	
		
    	AmazonS3 s3Client = new AmazonS3Client(credentials);

 
      	s3Client.setEndpoint("http://s3-us-west-1.amazonaws.com");
    //    String bucketName = existingUser;
      	String bucketName = mongo.getBucketName(existingUser);
      	String key = getFileNameFromPath(filePath);
      	
      	File file = new File("D:\\S3files\\" + key);
      	long fileSize = getFileSize(filePath);
      //	File file = new File(filePath);
      	System.out.println(key);
      	if(file.exists())
      	{
      	System.out.println("Yeayyyy file exists");
      		try {
    			s3Client.putObject(new PutObjectRequest(bucketName, key, file));
    			mongo.addNewFileDetails(existingUser, key, filePath, fileSize);
    			 return Response.status(200).entity("File Added").build();
    		} catch (AmazonServiceException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (AmazonClientException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
      	}
      	else
      		System.out.println("No not found");
      	
    	
        System.out.println();
        return Response.status(400).entity("File could not be Added").build();
   	
    }
   //This method copies file from one bucket to other 
    void copyFileFromBucket(String existinguser, String sharewith, String fileName){
    	AmazonCredentials myCredentials = new AmazonCredentials();
		AWSCredentials credentials = myCredentials.getCredentials();
    	System.out.println("Inside copy-file");
    	
		
    	AmazonS3 s3Client = new AmazonS3Client(credentials);

 
      	s3Client.setEndpoint("http://s3-us-west-1.amazonaws.com");
         
         // List to store copy part responses.
      	String targetObjectKey = fileName;
      	String targetBucketName = sharewith;
      	String sourceBucketName = existinguser;
      	String sourceObjectKey  = fileName;

         List<CopyPartResult> copyResponses =
                   new ArrayList<CopyPartResult>();
                           
         InitiateMultipartUploadRequest initiateRequest = 
         	new InitiateMultipartUploadRequest(targetBucketName, targetObjectKey);
         
         InitiateMultipartUploadResult initResult = 
         	s3Client.initiateMultipartUpload(initiateRequest);

         try {
             // Get object size.
             GetObjectMetadataRequest metadataRequest = 
             	new GetObjectMetadataRequest(sourceBucketName, sourceObjectKey);

             ObjectMetadata metadataResult = s3Client.getObjectMetadata(metadataRequest);
             long objectSize = metadataResult.getContentLength(); // in bytes

             // Copy parts.
             long partSize = 5 * (long)Math.pow(2.0, 20.0); // 5 MB

             long bytePosition = 0;
             for (int i = 1; bytePosition < objectSize; i++)
             {
             	CopyPartRequest copyRequest = new CopyPartRequest()
                    .withDestinationBucketName(targetBucketName)
                    .withDestinationKey(targetObjectKey)
                    .withSourceBucketName(sourceBucketName)
                    .withSourceKey(sourceObjectKey)
                    .withUploadId(initResult.getUploadId())
                    .withFirstByte(bytePosition)
                    .withLastByte(bytePosition + partSize -1 >= objectSize ? objectSize - 1 : bytePosition + partSize - 1) 
                    .withPartNumber(i);

                 copyResponses.add(s3Client.copyPart(copyRequest));
                 bytePosition += partSize;

             }
             CompleteMultipartUploadRequest completeRequest = new 
             	CompleteMultipartUploadRequest(
             			targetBucketName,
             			targetObjectKey,
             			initResult.getUploadId(),
             			GetETags(copyResponses));

             CompleteMultipartUploadResult completeUploadResponse =
                 s3Client.completeMultipartUpload(completeRequest);
         } catch (Exception e) {
         	System.out.println(e.getMessage());
         }
      }
      
  
    

// Helper function that constructs ETags.
static List<PartETag> GetETags(List<CopyPartResult> responses)
{
    List<PartETag> etags = new ArrayList<PartETag>();
    for (CopyPartResult response : responses)
    {
        etags.add(new PartETag(response.getPartNumber(), response.getETag()));
    }
    return etags;
} 
    @POST
	@Path("/old/{existinguser}/share")
	@Timed(name = "share-file")
	public Response shareFile(@PathParam("existinguser") String existingUser,
			@QueryParam("filename") String fileName,
			@QueryParam("sharewith") String sharewith) throws UnknownHostException {
		if (mongo.isUserNameExist(sharewith)) {
			mongo.shareFile(existingUser, sharewith, fileName);
			copyFileFromBucket(existingUser, sharewith, fileName);
			return Response.status(200).build();
		} else {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("Sorry we couldn't find the username you have provided")
					.build();
		}
	}

    @DELETE
    @Path("/old/{existinguser}/delete")
    @Timed(name = "delete-file")
 //      public Response delFile(@PathParam("filename") String key) {
    public Response deleteFile(@PathParam("existinguser") String existingUser, @QueryParam("filename") String fileName){
    //	AmazonS3 s3Client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
   
    AmazonCredentials myCredentials = new AmazonCredentials();
    AWSCredentials credentials = myCredentials.getCredentials();
    System.out.println("Inside upload-file");
   
    AmazonS3 s3Client = new AmazonS3Client(credentials);

 
      s3Client.setEndpoint("http://s3-us-west-1.amazonaws.com");
    //    String bucketName = existingUser;
      String bucketName = mongo.getBucketName(existingUser);
      String key = fileName;
      System.out.println(key);
     
      try {
      System.out.println("Deleting an object\n");
      s3Client.deleteObject(bucketName, key);
      mongo.deleteFileDetails(existingUser, fileName);
      return Response.status(200).entity("File Deleted").build();
    } catch (AmazonServiceException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } catch (AmazonClientException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } 

        System.out.println();
        return Response.status(400).entity("File could not be deleted").build();

    }
    
    @DELETE
	@Path("/old/{existinguser}/share/delete")
	@Timed(name = "delete-shared-file")
	public Response deleteSharedFile(
			@PathParam("existinguser") String existingUser,
			@QueryParam("fileName") String fileName) {
    	AmazonCredentials myCredentials = new AmazonCredentials();
        AWSCredentials credentials = myCredentials.getCredentials();
        System.out.println("Inside upload-file");
       
        AmazonS3 s3Client = new AmazonS3Client(credentials);

     
          s3Client.setEndpoint("http://s3-us-west-1.amazonaws.com");
        //    String bucketName = existingUser;
          String bucketName = mongo.getBucketName(existingUser);
          String key = fileName;
          System.out.println(key);
         
          try {
          System.out.println("Deleting an object\n");
          s3Client.deleteObject(bucketName, key);
          mongo.deleteSharedFileDetails(existingUser, fileName);
          return Response.status(200).entity("File Deleted").build();
        } catch (AmazonServiceException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } catch (AmazonClientException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } 

            System.out.println();
            return Response.status(400).entity("File could not be deleted").build();
	}
}


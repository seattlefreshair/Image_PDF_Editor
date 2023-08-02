package com.example.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.im4java.core.*;
import org.im4java.process.ArrayListOutputConsumer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@Api(tags = "首页模块")
@RestController
public class IndexController {
    @ApiImplicitParam(name = "name",value = "姓名",required = true)
    @ApiOperation(value = "向客人问好")
    @GetMapping("/sayHi")
    public GMOperation sayHi(@RequestParam(value = "name") String name) throws Exception {

        IdentifyCmd identifyCmd = new IdentifyCmd(true);

//        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
//        identifyCmd.setOutputConsumer(output);

//        Info info = new Info("/Users/kyle/Internship/scratch/Sunflower_from_Silesia2.jpeg", true);
//        // Extract width and height from the image information
//        int width = info.getImageWidth();
//        int height = info.getImageHeight();

        GMOperation op = new GMOperation();
        op.addImage("/Users/kyle/Internship/scratch/Sunflower_from_Silesia2.jpeg");
        op.format("%w"); // Format output as width,height
        identifyCmd.run(op);
//        ArrayList<String> cmdOutput = output.getOutput();
//        // todo iterate this
//        String dimensions = cmdOutput.get(0);
//        return ResponseEntity.ok("Hi: "+name + " my name is Kyle." );
        return op;
    }

    // Remove outside borders
    @ApiImplicitParam(name = "picDirectory",value = "Directory of the image to be processed",required = true)
    @ApiOperation(value = "Remove Black Edges")
    @PostMapping("/removeBlackEdges")
    public ResponseEntity<String> removeBlackEdges (@RequestParam(value = "picDirectory") String picDirectory) throws Exception{
        ConvertCmd cmd = new ConvertCmd(true);
        GMOperation op = new GMOperation();
        op.addImage(picDirectory);
        
        // Set the fuzz factor to consider near-black colors as black
        op.fuzz(10.0);

        // Set the background of the image to have no background after it has been trimmed
        op.background("none");

        // Trim the image to remove the background
        op.trim();
        op.addImage("/Users/kyle/Internship/scratch/removedBlackEdges.jpeg");
        cmd.run(op);

        return ResponseEntity.ok("Edges Removed");
    }

    // Auto-orient an image to prevent skewing
    @ApiImplicitParams({
        @ApiImplicitParam(name = "picDirectory",value = "Directory of the image to be processed",required = true),
        @ApiImplicitParam(name = "skewX", value = "Shearing factor for the X axis, as a double", required = true),
        @ApiImplicitParam(name = "skewY", value = "Shearing factor for the Y axis, as a double", required = true)
    })
    @ApiOperation(value = "Fix a Skewed Image")
    @PostMapping("/fixSkewing")
    public ResponseEntity<String> fixSkewing(
            @RequestParam(value = "picDirectory") String picDirectory,
            @RequestParam(value = "skewX") double skewX,
            @RequestParam(value = "skewY") double skewY

    ) throws Exception {
        ConvertCmd cmd = new ConvertCmd(true);
        GMOperation op = new GMOperation();

        op.addImage(picDirectory);

//        op.affine(1.0, -skewY, -skewX, 1.0, 0.0, 0.0);
        op.transform().shear(skewX, skewY);
//        op.autoOrient();

        op.addImage("/Users/kyle/Internship/scratch/fixSkewing.jpeg");
        cmd.run(op);

        return ResponseEntity.ok("Image is no longer skewed");
    }

    // Remove stains from an image
    @ApiImplicitParam(name = "picDirectory",value = "Directory of the image to be processed", required = true)
    @ApiOperation(value = "Remove Stains")
    @PostMapping("/removeStains")
    public ResponseEntity<String> removeStains (@RequestParam(value = "picDirectory") String picDirectory) throws Exception{
        ConvertCmd cmd = new ConvertCmd(true);
        GMOperation op = new GMOperation();

        op.addImage(picDirectory);

//        op.negate();
//        op.threshold(50, true);
//        op.negate();

        // Filter the image to remove noise and black dots
        op.noise(1.0);
        op.blur(0.5);

        // Apply a binary threshold to convert gray/black pixels to white
        op.threshold(50);

        op.addImage("/Users/kyle/Internship/scratch/noStains.jpeg");

        cmd.run(op);

        return ResponseEntity.ok("Stains have been removed");
    }

    // Crop a picture
    @ApiImplicitParams({
        @ApiImplicitParam(name = "picDirectory",value = "Directory of the image to be processed", required = true),
        @ApiImplicitParam(name = "height",value = "Height of the cropped image, starting from the top (in pixels)", required = true),
        @ApiImplicitParam(name = "width",value = "Width of the cropped image, starting from the left (in pixels)", required = true),
        @ApiImplicitParam(name = "x_position",value = "Move horizontally where image starts to get cropped (in pixels)", required = true),
        @ApiImplicitParam(name = "y_position",value = "Move vertically where image starts to get cropped (in pixels)", required = true)
    })
    @ApiOperation(value = "Picture Crop")
    @PostMapping("/crop")
    public ResponseEntity<String> crop(
            @RequestParam(value = "picDirectory") String picDirectory,
            @RequestParam(value = "height") int height,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "x_position") int x,
            @RequestParam(value = "y_position") int y

    ) throws Exception {
        ConvertCmd cmd = new ConvertCmd(true);

        GMOperation op = new GMOperation();

        op.addImage(picDirectory);
        op.crop(height,width,x,y);

        op.addImage("/Users/kyle/Internship/scratch/GMOcrop2.jpeg");

        cmd.run(op);

        return ResponseEntity.ok("Image has been cropped");
    }

    // Rotate an image by a certain amount of degrees
    @ApiImplicitParams({
        @ApiImplicitParam(name = "picDirectory", value = "Directory of the image to be processed", required = true),
        @ApiImplicitParam(name = "degrees", value = "Amount of degrees the photo is rotated", required = true)

    })
    @PostMapping("/rotate")
    @ApiOperation(value = "Rotate")
    public ResponseEntity<String> rotate(
            @RequestParam(value = "picDirectory") String picDirectory,
            @RequestParam(value = "degrees") Double degrees
        ) throws Exception {

        ConvertCmd cmd = new ConvertCmd(true);

        GMOperation op = new GMOperation();

        op.addImage(picDirectory);

        op.rotate(degrees);

        op.addImage("/Users/kyle/Internship/scratch/rotatedImage.jpeg");

        cmd.run(op);

        return ResponseEntity.ok("Hi I rotated your image by " + degrees + " degrees");
    }

    // Compress and change the image quality
    @ApiImplicitParams({
            @ApiImplicitParam(name = "picDirectory", value = "Directory of the image to be processed", required = true),
            @ApiImplicitParam(name = "quality", value = "Reduce the quality of the image. Highest quality is at 100.0," +
                    "while the lowest is at 0.0", required = true)
    })
    @ApiOperation(value = "Compression")
    @PostMapping("/compress")
    public ResponseEntity<String> compress (
            @RequestParam(value = "quality") Double percentageQuality,
            @RequestParam(value = "picDirectory") String picDirectory
    ) throws Exception {

        ConvertCmd cmd = new ConvertCmd(true);
        GMOperation op = new GMOperation();

        op.addImage(picDirectory);
        op.quality(percentageQuality);
        op.addImage("/Users/kyle/Internship/scratch/reducedQuality.jpeg");
        cmd.run(op);

        return ResponseEntity.ok("Your image has been compressed");
    }

    // Add a watermark onto an image
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pdfDirectory", value = "Directory of the pdf to be processed", required = true),
            @ApiImplicitParam(name = "font", value = "Font of the watermark", required = true),
            @ApiImplicitParam(name = "font_size", value = "Size of the watermark", required = true),
            @ApiImplicitParam(name = "font_color", value = "Hex color of the watermark", required = true)
    })
    @ApiOperation(value = "Text Watermark")
    @PostMapping("/textWatermark")
    public ResponseEntity<String> textWatermark (
            @RequestParam(value = "pdfDirectory") String pdfDirectory,
            @RequestParam(value = "font") String font,
            @RequestParam(value = "font_size") int font_size,
            @RequestParam(value = "font_color") String font_color
    ) throws Exception {
        ConvertCmd cmd = new ConvertCmd(true);
        GMOperation op = new GMOperation();

        int density = 300;
        op.density(density);
        op.addImage(pdfDirectory);

        op.background("transparent")
            .font(font)
//                .matte()
            .gravity("Center")
            .pointsize(font_size)
//            .fill("#808080") Grey
//            .fill("#ede4e4") Lighter Grey
            .fill(font_color)
            .draw("text 0,0 'Watermark'")
            ;  // Define watermark properties

        String outputFile = "/Users/kyle/Internship/scratch/textWatermarked.pdf";
        op.addImage(outputFile);
        cmd.run(op);

//        ProcessBuilder pb = new ProcessBuilder("gs", "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=pdfwrite",
//                "-sOutputFile=/Users/kyle/Internship/scratch/finalWatermarked.pdf", "-dFirstPage=1", "-dLastPage=1", picDirectory, "watermarked.pdf");
//        pb.redirectErrorStream(true);
//        Process process = pb.start();
//        process.waitFor();

        return ResponseEntity.ok("Your text watermark has been added: " + outputFile);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileDirectory", value = "Directory of the pdf to be processed", required = true),
            @ApiImplicitParam(name = "imageWatermarkDirectory", value = "Directory of the image to be used as a watermark", required = true),
            @ApiImplicitParam(name = "isPDF", value = "Boolean if output file should be a pdf", required = true),
            @ApiImplicitParam(name = "isRotated", value = "Boolean if the watermark is rotated", required = true)
    })
    @ApiOperation(value = "Image or PDF Watermark")
    @PostMapping("/imagePDFWatermark")
    public ResponseEntity<String> imagePDFWatermark (
            @RequestParam(value = "fileDirectory") String fileDirectory,
            @RequestParam(value = "imageWatermarkDirectory") String imageWatermarkDirectory,
            @RequestParam(value = "isPDF") boolean isPDF,
            @RequestParam(value = "isRotated") boolean isRotated
    ) throws Exception {
        // step 1: gm convert +shade 30x60 cockatoo.miff mask.miff
        ConvertCmd cmd = new ConvertCmd(true);
        GMOperation op_for_convert = new GMOperation();

        // input file path
        op_for_convert.addImage(fileDirectory);
        op_for_convert.p_shade(30.00, 30.00);

        // temp output file path
        String mask_output_file = "/Users/kyle/Internship/scratch/mask.jpeg";
        op_for_convert.addImage(mask_output_file);

        // run convert to generate a greyscale version of the input file
        cmd.run(op_for_convert);

        // step 2: Composite
        CompositeCmd compositeCmd = new CompositeCmd(true);
        GMOperation op = new GMOperation();

        // Note: compose is manually added support in im4java. That's why we use
        // a local jar file instead of the ones from the maven repository
        op.compose("bumpmap");

        String outputFile = "/Users/kyle/Internship/scratch/imageWatermarked.jpeg";
        if (isPDF)
        {
            // For resolution purposes, high memory density
            int density = 400;
            op.density(density);
            outputFile = "/Users/kyle/Internship/scratch/pdfWatermarked.pdf";
        }

//      Rotate the watermark
        if (isRotated) {
            ConvertCmd rotateCmd = new ConvertCmd(true);
            GMOperation rotateOP = new GMOperation();
            rotateOP.addImage(imageWatermarkDirectory);
            rotateOP.rotate(45.);
            String rotatedWatermark = "/Users/kyle/Internship/scratch/rotatedWatermark.jpeg";
            rotateOP.addImage(rotatedWatermark);
            rotateCmd.run(rotateOP);
            imageWatermarkDirectory = rotatedWatermark;
        }

        op.addImage(imageWatermarkDirectory, fileDirectory, mask_output_file);
        op.gravity("center");

        // Get the height and width of the input file

        Info info = new Info("/Users/kyle/Internship/scratch/Sunflower_from_Silesia2.jpeg", true);
        // Extract width and height from the image information
        int width = info.getImageWidth();
        int height = info.getImageHeight();

//        ConvertCmd paramCmd = new ConvertCmd(true);
//        GMOperation paramOp = new GMOperation();
//        paramOp.format("%w,%h"); // Format output as width,height
//        paramOp.addImage(imageWatermarkDirectory);
//
//        // Execute the command with the specified operation
//        paramOp.addImage("/Users/kyle/Internship/scratch/proportions.jpeg");
//        paramCmd.run(paramOp);

//        // Get the dimensions of the input image
//        IdentifyCmd identifyCmd = new IdentifyCmd(true);
//        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
//        identifyCmd.setOutputConsumer(output);
//        identifyCmd.run(op);
//        String dimensions = output.getOutput().get(0);
//        String[] parts = dimensions.split(" ");
//        int width = Integer.parseInt(parts[2]);
//        int height = Integer.parseInt(parts[3]);
//        // Resize the watermark to match the dimensions of the input image
//        op.resize();

        op.addImage(outputFile);
        compositeCmd.run(op);

//        String maskImage = "/Users/kyle/Internship/scratch/gray1.jpeg";
//        String maskImage = "/Users/kyle/Internship/scratch/Windows_black_edges.png";
//        op.addImage(imageWatermarkDirectory, pdfDirectory, maskImage);
//        op.addImage(imageWatermarkDirectory, pdfDirectory);
//        op.addImage(pdfDirectory);

//        op.background("transparent");

//        String outputFile = "/Users/kyle/Internship/scratch/imageWatermarked.pdf";

//        op.channel("Matte");
//        op.addImage(outputFile);
//        cmd.run(op);
        return ResponseEntity.ok("Your image watermark has been added: " + outputFile);
    }

    // Add a watermark onto an image
    @ApiImplicitParams({
            @ApiImplicitParam(name = "picDirectory", value = "Directory of the image to be processed", required = true)
    })
    @ApiOperation(value = "Add Annotations")
    @PostMapping("/annotate")
    public ResponseEntity<String> annotate (
            @RequestParam(value = "picDirectory") String picDirectory
    ) throws Exception {
        ConvertCmd cmd = new ConvertCmd(true);
        GMOperation op = new GMOperation();

        int density = 300;
        op.density(density);

        op.addImage(picDirectory);
        // text is somewhat working
        op.draw("text 100,100 'This is a comment'");
//        op.draw("@/Users/kyle/Internship/scratch/text");
        op.addImage("/Users/kyle/Internship/scratch/annotatedImage.jpeg");
        cmd.run(op);
        return ResponseEntity.ok("Your comment has been added");
    }
    @PostMapping("/testpost")
    public ResponseEntity<String> TestPost(
            @RequestParam(value = "degree") String degree,
            @RequestParam(value = "angle") String angle,
            @RequestBody String requestBody) throws Exception {

        return ResponseEntity.ok("test posting" + requestBody);
    }

//    @ApiOperation(value = "普通body请求+Param+Header+Path")
//    @ApiImplicitParam ({
//            @ApiImplicitParam(name = "id",value = "id",in = APIParameterIParameterIn.PATH),
//            @ApiImplicitParam(name = "token",description = "请求token",required = true,in = ParameterIn.HEADER),
//            @ApiImplicitParam(name = "name",description = "文件名称",required = true,in=ParameterIn.QUERY)
//    })
//    @PostMapping("/bodyParamHeaderPath/{id}")
//    public ResponseEntity<FileResp> bodyParamHeaderPath(@PathVariable("id") String id,@RequestHeader("token") String token, @RequestParam("name")String name,@RequestBody FileResp fileResp){
//        fileResp.setName(fileResp.getName()+",receiveName:"+name+",token:"+token+",pathID:"+id);
//        return ResponseEntity.ok(fileResp);
//    }
}

//import org.im4java.core.ConvertCmd;
//        import org.im4java.core.IMOperation;
//
//public class ImageBorderRemover {
//    public static void main(String[] args) {
//        String inputImagePath = "path/to/input/image.jpg";
//        String outputImagePath = "path/to/output/image.jpg";
//
//        try {
//            // Create ConvertCmd instance
//            ConvertCmd cmd = new ConvertCmd();
//
//            // Create IMOperation instance
//            IMOperation operation = new IMOperation();
//            operation.addImage(inputImagePath);
//
//            // Set border color and geometry
//            operation.bordercolor("black").border(1);
//
//            // Remove black borders
//            operation.shave(1x1);
//
//            // Set output image path
//            operation.addImage(outputImagePath);
//
//            // Execute the operation
//            cmd.run(operation);
//
//            System.out.println("Image borders removed successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}


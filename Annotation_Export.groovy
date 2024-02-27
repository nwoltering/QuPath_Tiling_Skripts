import qupath.lib.images.servers.LabeledImageServer

def imageData = getCurrentImageData()

// Define output path (relative to project)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathOutput = buildFilePath(PROJECT_BASE_DIR, 'export', name)
mkdirs(pathOutput)

// Define output resolution
double requestedPixelSize = 2.0

// Convert to downsample
double downsample = requestedPixelSize / imageData.getServer().getPixelCalibration().getAveragedPixelSize()

// Create an ImageServer where the pixels are derived from annotations
def labelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) // Specify background label (usually 0 or 255)
    .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
    .addLabel('Lymphocyte', 1)      // Choose output labels (the order matters!)
    .addLabel('Monocyte', 2)
    .addLabel('Activated Monocyte', 3)
    .addLabel('Macrophage', 4)
    .addLabel('Activated Lymphocyte', 5)
    .addLabel('Plasma cell', 6)
    .addLabel('Neutrophilic granulocyte', 7)
    .addLabel('Eosinophilic granulocyte', 8)
    .addLabel('Erythrocyte', 9)
    .addLabel('Erythrophage', 10)
    .addLabel('Hemosiderophage', 11)
    .addLabel('Hematoidin crystal', 12)
    .addLabel('Mitosis', 13)
    .addLabel('Unknown cell', 14)
    .addLabel('Tumor', 15)
    .addLabel('TBD', 16)
    .addLabel('Unknown Object', 17)
    .addLabel('Cell shadow', 18)
    .addLabel('Artificial cell', 19)
    .addLabel('Autolytic cell', 20)
    .lineThickness(0)          // Optionally export annotation boundaries with another label
    .setBoundaryLabel('Boundary*', 4) // Define annotation boundary label
    .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
    .build()

// Create an exporter that requests corresponding tiles from the original & labeled image servers
new TileExporter(imageData)
    .downsample(downsample)     // Define export resolution
    .imageExtension('.jpg')     // Define file extension for original pixels (often .tif, .jpg, '.png' or '.ome.tif')
    .tileSize(512)              // Define size of each tile, in pixels
    .labeledServer(labelServer) // Define the labeled image server to use (i.e. the one we just built)
    .annotatedTilesOnly(true)  // If true, only export tiles if there is a (labeled) annotation present
    .overlap(64)                // Define overlap, in pixel units at the export resolution
    .writeTiles(pathOutput)     // Write tiles to the specified directory
    

// Export each region
int i = 0
for (annotation in getAnnotationObjects()) {
    def region = RegionRequest.createInstance(
        labelServer.getPath(), downsample, annotation.getROI())
    i++
    def outputPath = buildFilePath(pathOutput, 'Region ' + i + '.png')
    writeImageRegion(labelServer, region, outputPath)
}
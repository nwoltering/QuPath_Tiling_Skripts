import qupath.lib.images.servers.LabeledImageServer

def imageData = getCurrentImageData()

// Define output path (relative to project)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def outputDir = buildFilePath('C:\\Daten\\Tiles', 'export') // Double Backslash als Escape Character hat auch funktioniert
mkdirs(outputDir)
def path = buildFilePath(outputDir, name + "-labels.png")

// Define how much to downsample during export (may be required for large images)
double downsample = 8

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
  .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
  .build()

// Write the image
writeImage(labelServer, path) // Das exportiert wohl nur eine Version, in der das Bild als ganzes mit farbigen Labels annotiert exportiert wird. Es wird kein Tiling durchgeführt
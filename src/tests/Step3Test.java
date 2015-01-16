package tests;

import com.trabauer.twitchtools.worker.TwitchDownloadWorker;
import com.trabauer.twitchtools.gui.vod.download.DownloadProgressPanel;
import com.trabauer.twitchtools.model.twitch.TwitchDownloadQueue;
import com.trabauer.twitchtools.model.twitch.TwitchVideo;
import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Flo on 22.11.2014.
 *
 * Just a TestClass to test only the Download of a File.
 */
public class Step3Test {

    public static final String TWITCH_URL_STRING = "http://www.twitch.tv/taketv/b/590565753"; // ESL Pro Series Winter
    public static final String DESTINATION_DIR = "E:\\twitch\\";
    public static final String FILENAME = "$(channel)/$(game)/$(title)";
    public static final String QUALITY = "source";
    public static final int THREADCOUNT = 4;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    TestController controller = new TestController();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class TestController /*implements PropertyChangeListener*/ {

        public DownloadProgressPanel downloadBarPanel[];

        public TestController() throws MalformedURLException {
            JFrame testMainFrame = new JFrame("Step 3 Download Test");
            JPanel contentPane = new JPanel();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

            TwitchVideo twitchVideo = new TwitchVideo(new URL(TWITCH_URL_STRING));


            testMainFrame.setSize(new Dimension(800, 600));
            testMainFrame.setContentPane(contentPane);



            // UserInput
            // http://www.twitch.tv/taketv/b/590565753



            // 1st generate DownloadList

            String quality = twitchVideo.getBestAvailableQuality();
            ArrayList<TwitchVideoPart> twitchVideoParts = twitchVideo.getTwitchVideoParts(quality);
            TwitchDownloadQueue downloadQueue = new TwitchDownloadQueue(twitchVideoParts);

            downloadBarPanel = new DownloadProgressPanel[THREADCOUNT];
            TwitchDownloadWorker[] twitchDownloadWorkers = new TwitchDownloadWorker[THREADCOUNT];


            for(int i=0; i<THREADCOUNT; i++) {

                TwitchVideoPart twitchVideoPart = downloadQueue.peekNextVideoPart();
                downloadBarPanel[i] = new DownloadProgressPanel(downloadQueue.getInitialSize());
                String labelText = "%3d / %3d ";
                labelText = String.format(labelText, twitchVideoPart.getPartNumber(), twitchVideo.getTwitchVideoParts(QUALITY).size());
                downloadBarPanel[i].setPrefixText(labelText);
                downloadBarPanel[i].setPostfixText("Blabla " + String.valueOf(i));

                contentPane.add(downloadBarPanel[i]);

                twitchDownloadWorkers[i] = new TwitchDownloadWorker(new File(DESTINATION_DIR + "590565753"), downloadQueue);
                //twitchDownloadTasks[i].addPropertyChangeListener(this);
                twitchDownloadWorkers[i].addPropertyChangeListener(downloadBarPanel[i]);
                twitchDownloadWorkers[i].execute();
            }













            testMainFrame.setVisible(true);
        }

        /*
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println(" PropertyChanged " + evt.getPropertyName() + " from " + evt.getOldValue() + " to " + evt.getNewValue());
            if(evt.getPropertyName().equals("progress")) {
                int progress = (Integer) evt.getNewValue();
                downloadBarPanel


            }

        }
        */
    }

}

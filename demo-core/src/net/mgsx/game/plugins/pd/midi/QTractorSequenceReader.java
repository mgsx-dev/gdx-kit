package net.mgsx.game.plugins.pd.midi;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class QTractorSequenceReader
{
	// TODO maybe get markers from XML ... 
	public SequenceDesc read(FileHandle file) throws IOException{
		SequenceDesc desc = new SequenceDesc();
		XmlReader xml = new XmlReader();
		Element root = xml.parse(file);
		
		// TODO get from header.
		int bpm = 95;
		int sampleRate = 48000;
		int tickPerBeat = 96;
		
		for(Element track : root.getChildByName("tracks").getChildrenByName("track")){
			if("midi".equals(track.get("type"))){
				for(Element clip : track.getChildByName("clips").getChildrenByName("clip")){
					Element props = clip.getChildByName("properties");
					
					String clipName = props.get("name");
					long start = Long.parseLong(props.get("start"));
					long offset = Long.parseLong(props.get("offset"));
					long length = Long.parseLong(props.get("length"));
					
					
					start = ((tickPerBeat * start * bpm) / 60) / sampleRate;
					offset = ((tickPerBeat * offset * bpm) / 60) / sampleRate;
					length = ((tickPerBeat * length * bpm) / 60) / sampleRate;
					
					SequenceMarker markerStart = new SequenceMarker();
					markerStart.name = clipName + ".start";
					markerStart.tick = offset + start;
					SequenceMarker markerEnd = new SequenceMarker();
					markerEnd.name = clipName + ".end";
					markerEnd.tick = offset + start + length;
					
					desc.markers.put(markerStart.name, markerStart);
					desc.markers.put(markerEnd.name, markerEnd);
				}
			}
		}
		return desc;
	}
}

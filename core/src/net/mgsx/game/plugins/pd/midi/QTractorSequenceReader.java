package net.mgsx.game.plugins.pd.midi;

import java.io.IOException;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class QTractorSequenceReader
{
	protected int sampleRate, bpm, midiResolution;
	
	public QTractorSequenceReader(int midiResolution) {
		super();
		this.midiResolution = midiResolution;
	}

	protected long convertTick(long qtrTick)
	{
		double time = qtrTick / (double)sampleRate;
		double beat = time * bpm / 60.f;
		double midiTick = beat * midiResolution;
		long result = Math.round(midiTick);
		return result;
	}


	public SequenceDesc read(FileHandle file) throws IOException{
		SequenceDesc desc = new SequenceDesc();
		XmlReader xml = new XmlReader();
		Element root = xml.parse(file);
		
		Element sessionProperties = root.getChildByName("properties");
		sampleRate = Integer.parseInt(sessionProperties.getChildByName("sample-rate").getText());
		bpm = Integer.parseInt(sessionProperties.getChildByName("tempo").getText());
		
		for(Element marker : root.getChildByName("markers").getChildrenByName("marker")){
			SequenceMarker m = new SequenceMarker();
			m.name = marker.getChildByName("text").getText();
			m.tick = convertTick(Long.parseLong(marker.get("frame")));
			
			desc.markers.put(m.name, m);
		}
		for(Element track : root.getChildByName("tracks").getChildrenByName("track")){
			if("midi".equals(track.get("type"))){
				for(Element clip : track.getChildByName("clips").getChildrenByName("clip")){
					Element props = clip.getChildByName("properties");
					
					String clipName = props.get("name");
					long start = convertTick(Long.parseLong(props.get("start")));
					long length = convertTick(Long.parseLong(props.get("length")));
					
					SequenceMarker markerStart = new SequenceMarker();
					markerStart.name = clipName + ".start";
					markerStart.tick = start;
					SequenceMarker markerEnd = new SequenceMarker();
					markerEnd.name = clipName + ".end";
					markerEnd.tick = start + length;
					
					desc.markers.put(markerStart.name, markerStart);
					desc.markers.put(markerEnd.name, markerEnd);
				}
			}
		}
		if(Gdx.app.getLogLevel() == Application.LOG_DEBUG){
			for(SequenceMarker m : desc.markers.values()){
				Gdx.app.debug("MIDI", m.toString());
			}
		}
		
		return desc;
	}
}

package br.unb.mobileMedia.playlist;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;


public class ArrayAdapterMusic extends ArrayAdapter<Audio> {

	private Context context;
	private int id;
	private List<Audio> items;
	private Map<Long, String> map;
	private MediaExtractor audioExtractor;

	public ArrayAdapterMusic(Context context, int textViewResourceId,
			List<Audio> objects, Map<Long, String> map) {

		super(context, textViewResourceId, objects);

		
		this.context = context;
		this.id = textViewResourceId;
		this.items = objects;
		this.map = map;
		this.audioExtractor = new DefaultAudioExtractor(this.context);
		

	}
	
	
	public Audio getItem(int i) {
		return items.get(i);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
    		
		if (rowView == null) {
           LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           rowView = vi.inflate(id, null);
		}
		
		final Audio audio = items.get(position);

		if (audio != null) {
			
			ImageView icon = (ImageView) rowView.findViewById(R.id.AlbumArt);
			TextView titleAudio = (TextView) rowView.findViewById(R.id.TitleItem);
			TextView artistaAudio = (TextView) rowView.findViewById(R.id.ArtistaItem);
			TextView albumAudio = (TextView) rowView.findViewById(R.id.AlbumItem);
			TextView bitRate = (TextView) rowView.findViewById(R.id.bitRate);				
			
			icon.setImageBitmap(audioExtractor.getAlbumArt(audio.getUrl()));
			titleAudio.setText(audio.getTitle());
			albumAudio.setText(audio.getAlbum());
			artistaAudio.setText(map.get(audio.getAuthorId()));
			
			int b = Integer.parseInt(audioExtractor.getBitRate(audio.getUrl()));
			b = (b == 0) ? 0 : (b/1000);
			bitRate.setText(b+" kbps");
			
		}
		return rowView;
	}
	
	

}

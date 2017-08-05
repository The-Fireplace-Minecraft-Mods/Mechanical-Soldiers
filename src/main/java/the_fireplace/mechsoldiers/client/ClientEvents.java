package the_fireplace.mechsoldiers.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.network.PacketDispatcher;
import the_fireplace.mechsoldiers.network.packets.RequestPartsMessage;
import the_fireplace.mechsoldiers.util.StainedItemUtil;
import the_fireplace.overlord.Overlord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public final class ClientEvents {
	private static Random rand = new Random();
	private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
	public static int splashOffsetCount = 0;
	public static final int finalSplashOffsetCount;
	private static final List<String> mySplashes = Lists.newArrayList(
			"I'm sorry, Dave. I'm afraid I can't do that.",
			"...Painted?"
	);

	static {
		splashOffsetCount += mySplashes.size();

		//Using this system allows other mods using the system to know how many mod-added splashes there are. Not perfect, but Forge doesn't have a system in place, so this will have to do.
		try{
			File file = new File(".splashes");
			if(file.exists()) {
				byte[] encoded = Files.readAllBytes(file.toPath());
				try {
					splashOffsetCount += Integer.parseInt(new String(encoded, "UTF-8"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if(!file.delete())
					Overlord.logWarn("Splashes file could not be deleted");
			}
			file.createNewFile();
			file.deleteOnExit();
			FileWriter fw = new FileWriter(file);
			fw.write(String.valueOf(splashOffsetCount));
			fw.close();
		}catch(IOException e){
			Overlord.logWarn(e.getLocalizedMessage());
		}
		finalSplashOffsetCount = splashOffsetCount;
	}

	@SubscribeEvent
	public static void screenload(GuiScreenEvent.InitGuiEvent event) {
		if (event.getGui() instanceof GuiMainMenu) {
			IResource iresource = null;
			try {
				List<String> defaultSplashes = Lists.newArrayList();
				iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
				String s;

				while ((s = bufferedreader.readLine()) != null) {
					s = s.trim();

					if (!s.isEmpty()) {
						defaultSplashes.add(s);
					}
				}

				int splashNum = rand.nextInt(defaultSplashes.size() + finalSplashOffsetCount);

				if (splashNum >= defaultSplashes.size()+finalSplashOffsetCount-mySplashes.size())
					ReflectionHelper.setPrivateValue(GuiMainMenu.class, (GuiMainMenu) event.getGui(), mySplashes.get(splashNum - (defaultSplashes.size()+finalSplashOffsetCount-mySplashes.size())), "splashText", "field_73975_c");
			} catch (IOException e) {
				Overlord.logWarn(e.getLocalizedMessage());
			} finally {
				IOUtils.closeQuietly(iresource);
			}
		}
	}

	@SubscribeEvent
	public static void modelRegister(ModelRegistryEvent event){
		MechSoldiers.registerItemRenders();
	}

	@SubscribeEvent
	public static void renderTooltip(ItemTooltipEvent event) {
		if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey("StainColor"))
			event.getToolTip().add(Overlord.proxy.translateToLocal("stained"));
	}

	@SubscribeEvent
	public static void renderTooltip(RenderTooltipEvent.Pre event) {
		if (event.getStack().hasTagCompound() && event.getStack().getTagCompound().hasKey("StainColor")) {
			int mouseX = event.getX();
			int mouseY = event.getY();
			int screenWidth = event.getScreenWidth();
			int screenHeight = event.getScreenHeight();
			int maxTextWidth = event.getMaxWidth();
			FontRenderer font = event.getFontRenderer();
			List<String> textLines = event.getLines();

			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int tooltipTextWidth = 0;
			int color = -1;
			for (String line : event.getLines()) {
				int textLineWidth = font.getStringWidth(line);

				if (textLineWidth > tooltipTextWidth)
					tooltipTextWidth = textLineWidth;
			}
			boolean needsWrap = false;

			int titleLinesCount = 1;
			int tooltipX = mouseX + 12;
			if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
				tooltipX = mouseX - 16 - tooltipTextWidth;
				if (tooltipX < 4) // if the tooltip doesn't fit on the screen
				{
					if (mouseX > screenWidth / 2)
						tooltipTextWidth = mouseX - 12 - 8;
					else
						tooltipTextWidth = screenWidth - 16 - mouseX;
					needsWrap = true;
				}
			}

			if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
				tooltipTextWidth = maxTextWidth;
				needsWrap = true;
			}

			if (needsWrap) {
				int wrappedTooltipWidth = 0;
				List<String> wrappedTextLines = new ArrayList<>();
				for (int i = 0; i < event.getLines().size(); i++) {
					String textLine = event.getLines().get(i);
					List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
					if (i == 0)
						titleLinesCount = wrappedLine.size();

					for (String line : wrappedLine) {
						int lineWidth = font.getStringWidth(line);
						if (lineWidth > wrappedTooltipWidth)
							wrappedTooltipWidth = lineWidth;
						wrappedTextLines.add(line);
					}
				}
				tooltipTextWidth = wrappedTooltipWidth;
				textLines = wrappedTextLines;

				if (mouseX > screenWidth / 2)
					tooltipX = mouseX - 16 - tooltipTextWidth;
				else
					tooltipX = mouseX + 12;
			}

			int tooltipY = mouseY - 12;
			int tooltipHeight = 8;

			if (textLines.size() > 1) {
				tooltipHeight += (textLines.size() - 1) * 10;
				if (textLines.size() > titleLinesCount)
					tooltipHeight += 2;
			}

			if (tooltipY + tooltipHeight + 6 > screenHeight)
				tooltipY = screenHeight - tooltipHeight - 6;

			final int zLevel = 300;
			final int backgroundColor = 0xF0100010;
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			final int borderColorStart = 0x505000FF;
			final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			GuiUtils.drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
			GuiUtils.drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

			for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
				String line = textLines.get(lineNumber);
				if (line == null)
					continue;
				if (TextFormatting.getTextWithoutFormattingCodes(line).equals(Overlord.proxy.translateToLocal("stained"))) {
					color = StainedItemUtil.getColor(event.getStack()).getRGB();
					line = TextFormatting.getTextWithoutFormattingCodes(line);
				} else
					color = -1;
				font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, color);

				if (lineNumber + 1 == titleLinesCount)
					tooltipY += 2;

				tooltipY += 10;
			}

			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void entityDamage(LivingHurtEvent e){
		if(e.getEntityLiving() instanceof EntityMechSkeleton)
			PacketDispatcher.sendToServer(new RequestPartsMessage((EntityMechSkeleton)e.getEntityLiving()));
	}
}
